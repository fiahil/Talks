package Kafka

import ES.IndexInto
import Pokemon.Pokemon
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
