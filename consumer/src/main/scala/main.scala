import java.util.Properties
import kafka.consumer.{Consumer, ConsumerConfig, KafkaStream}
import kafka.message.MessageAndMetadata

object main extends App {

  def getConfig = {
    val properties = new Properties()
    properties.put("zookeeper.connect", "localhost:2181")
    properties.put("group.id", "pogo_consumer")
    properties.put("auto.offset.reset", "largest")
    properties.put("zookeeper.session.timeout.ms", "400")
    properties.put("zookeeper.sync.time.ms", "200")
    properties.put("auto.commit.interval.ms", "500")
    new ConsumerConfig(properties)
  }

  def displayMessage(stream: KafkaStream[Array[Byte], Array[Byte]]) = {
    val it = stream.iterator()

    it.forEachRemaining(new java.util.function.Consumer[MessageAndMetadata[Array[Byte], Array[Byte]]] {
      override def accept(m: MessageAndMetadata[Array[Byte], Array[Byte]]): Unit = {

        println(s"${System.currentTimeMillis()}: ${new String(m.key())}=${new String(m.message())}")
      }
    })
  }

  val consumer = Consumer.create(getConfig)
  val streams = consumer.createMessageStreams(Map("dragons" -> 1))

  streams.get("dragons").get.foreach(displayMessage)
}
