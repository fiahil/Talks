package services.Kafka

import java.util.concurrent.TimeUnit

import akka.actor.{PoisonPill, ActorRef, ActorSystem}
import akka.util.Timeout
import kafka.consumer.KafkaStream
import kafka.message.MessageAndMetadata
import models.Pokemon

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object PokemonService {

  def cycle(system: ActorSystem)
           (stream: KafkaStream[Array[Byte], Array[Byte]])
           (implicit ec: ExecutionContext) = {

    // Getting an iterator on our stream for a simple consumption
    val it = stream.iterator()

    // Java boilerplate 2 : More suffering in Java-land
    val consumer = new java.util.function.Consumer[MessageAndMetadata[Array[Byte], Array[Byte]]] {

      // Function called for each message
      override def accept(mm: MessageAndMetadata[Array[Byte], Array[Byte]]): Unit = {

        val key = new String(mm.key())
        val msg = new String(mm.message())

        println(s"[pok] key = $key ; msg = $msg")

        // Convert message to pokemon
        Pokemon.fromMessage(mm.message()).foreach { pk =>
          implicit val timeout = Timeout(100, TimeUnit.MILLISECONDS)

          // Resolve the spawnpoint actor
          system.actorSelection("/user/" + key).resolveOne().onComplete {
            case Success(actor: ActorRef) => actor ! pk
            case Failure(_)               => println(s"Could not found actor $key")
          }
        }
      }
    }

    // Infinite loop over messages contained in our stream
    it.forEachRemaining(consumer)
  }
}
