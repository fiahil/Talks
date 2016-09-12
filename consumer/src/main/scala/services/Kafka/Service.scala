package services.Kafka

import services.ES.IndexInto
import models.Pokemon
import akka.actor.Actor

class Service extends Actor {

  override def receive: Receive = {
    case pk: Pokemon =>
      println("received")
      println(pk)
      IndexInto.pokemon(pk)
    case _ => println("oho")
  }
}
