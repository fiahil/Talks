import java.util.Properties
import ES.IndexInto
import Pokemon.Pokemon
import Web.Server
import kafka.consumer.{KafkaStream, Consumer, ConsumerConfig}
import kafka.message.MessageAndMetadata
import kafka.utils.Logging
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object main extends App with Logging {

  def getConfig = {
    val properties = new Properties()
    properties.put("bootstrap.servers", "localhost:9092")
    properties.put("group.id", "pogo_consumer")
    properties.put("auto.offset.reset", "largest")
    properties.put("zookeeper.connect", "localhost:2181")
    properties.put("zookeeper.session.timeout.ms", "400")
    properties.put("zookeeper.sync.time.ms", "200")
    properties.put("auto.commit.interval.ms", "500")
    new ConsumerConfig(properties)
  }

  def displayMessage(stream: KafkaStream[Array[Byte], Array[Byte]]) = {
    val it = stream.iterator()

    it.forEachRemaining(new java.util.function.Consumer[MessageAndMetadata[Array[Byte], Array[Byte]]] {
      override def accept(m: MessageAndMetadata[Array[Byte], Array[Byte]]): Unit = {

        Pokemon.fromMessage(m.message()).foreach { pk =>
          println(pk)
          IndexInto.pokemon(pk)
        }
      }
    })
  }

  println("Starting consumer")

  val consumer = Consumer.create(getConfig)
  val streams = consumer.createMessageStreams(Map("pokemons" -> 1))
  Future { streams.get("pokemons").get.foreach(displayMessage) }

  println("Consumer started")

  Server.start
}
