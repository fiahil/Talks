package services.Kafka

import java.util.Properties
import java.util.concurrent.Executors

import akka.actor.{Props, DeadLetter, ActorSystem}
import kafka.consumer.{Consumer, ConsumerConfig}
import scala.concurrent.{ExecutionContext, Future}

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

  // Our actor system managing our actors
  val system = ActorSystem("es-sharpshooter")

  // Taking care of dead letters
  system.eventStream.subscribe(system.actorOf(Props[IndexService], "dead-letters"), classOf[DeadLetter])

  // Dedicated Kafka Execution context
  implicit val KafkaContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(20))

  def start = {
    // Starting our consumer
    val consumer = Consumer.create(config)

    val topics = Map(
      "pokemons" -> 1,
      "spawnpoints" -> 1
    )

    val streams = consumer.createMessageStreams(topics)

    // Start the consumer asynchronously
    Future {
      streams.get("pokemons").get.foreach(PokemonService.cycle(system))
    } onFailure { case ec => println(ec) }
    Future {
      streams.get("spawnpoints").get.foreach(SpawnService.cycle(system))
    } onFailure { case ec => println(ec) }
  }
}
