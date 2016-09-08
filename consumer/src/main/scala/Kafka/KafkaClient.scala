package Kafka

import java.util.Properties

import akka.actor.ActorSystem
import kafka.consumer.{Consumer, ConsumerConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object KafkaClient {

  val config = {
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

  val system = ActorSystem("es-sharpshooter")

  def start = {
    val consumer = Consumer.create(config)
    val streams = consumer.createMessageStreams(Map("pokemons" -> 1))

    // Start the consumer asynchronously
    Future {
      streams.get("pokemons").get.foreach(Message.display(system))
    }
  }
}
