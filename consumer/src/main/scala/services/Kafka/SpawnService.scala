package services.Kafka

import java.util.concurrent.TimeUnit

import akka.actor.{PoisonPill, Props, ActorRef, ActorSystem}
import akka.util.Timeout
import kafka.consumer.KafkaStream
import kafka.message.MessageAndMetadata

import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}

object SpawnService {

  def cycle(system: ActorSystem)
           (stream: KafkaStream[Array[Byte], Array[Byte]])
           (implicit ec: ExecutionContext) = {

    // Getting an iterator on our stream for a simple consumption
    val it = stream.iterator()

    // Java boilerplate 2 : More suffering in Java-land
    val consumer = new java.util.function.Consumer[MessageAndMetadata[Array[Byte], Array[Byte]]] {

      // Function called for each message
      override def accept(mm: MessageAndMetadata[Array[Byte], Array[Byte]]): Unit = {

        // Destroy an actor at the end of the current processing queue.
        // A poison pill is enqueued like a regular message, except the targetted
        // actor will self destruct upon processing it.
        def sendPoisonPill(id: String) = {
          // Timeout for actor queries
          implicit val timeout = Timeout(1, TimeUnit.SECONDS)

          system.actorSelection("/user/" + id).resolveOne().onComplete {
            case Success(actor: ActorRef) => actor ! PoisonPill
            case Failure(_)               => println(s"Could not found actor $id (already dead?)")
          }
        }

        val key = new String(mm.key())
        val msg = new String(mm.message()).replace("\"", "")

        println(s"key = $key ; msg = $msg")

        key match {
          case "create" => system.actorOf(Props[Service], msg)
          case "delete" => sendPoisonPill(msg)
          case _        => println(s"Unknown operation: '$key'")
        }
      }
    }

    // Infinite loop over messages contained in our stream
    it.forEachRemaining(consumer)
  }
}
