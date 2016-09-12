package services.Kafka

import java.util.concurrent.TimeUnit

import models.Pokemon
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import kafka.consumer.KafkaStream
import kafka.message.MessageAndMetadata

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Message {

  def display(system: ActorSystem)(stream: KafkaStream[Array[Byte], Array[Byte]])(implicit ec: ExecutionContext) = {
    val it = stream.iterator()

    it.forEachRemaining(new java.util.function.Consumer[MessageAndMetadata[Array[Byte], Array[Byte]]] {
      override def accept(m: MessageAndMetadata[Array[Byte], Array[Byte]]): Unit = {

        Pokemon.fromMessage(m.message()).foreach { pk =>
          println(pk)
          implicit val timeout = Timeout(1, TimeUnit.SECONDS)
          // Fail to fetch actor
//          system.actorSelection("/user/" + pk.encounterId).resolveOne().onComplete {
//            case Success(actor: ActorRef) => actor ! pk
//            case Failure(_)               => system.actorOf(Props[Service], pk.encounterId) ! pk
//          }
        }
      }
    })
  }
}
