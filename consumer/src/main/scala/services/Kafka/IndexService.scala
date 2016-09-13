package services.Kafka

import akka.actor.{DeadLetter, Actor}
import models.Pokemon
import com.github.nscala_time.time.Imports._
import services.ES.{IndexInto, DeleteFrom}

class IndexService extends Actor {

  import context._

  def indexWithTTL(pokemon: Pokemon) = {
    // Index pokemon into ES and schedule another message containing the ID
    IndexInto.pokemon(pokemon).map { id =>
      val duration = (DateTime.now to pokemon.expireAt).toDuration.toScalaDuration

      system.scheduler.scheduleOnce(duration) {
        self ! id
      }
    }
  }

  def deleteAfterExpired(id: String) = {
    DeleteFrom.pokemon(id)
  }

  override def receive: Receive = {
    case pk: Pokemon               => indexWithTTL(pk)
    case id: String                => deleteAfterExpired(id)
    case DeadLetter(msg, from, to) => deleteAfterExpired(msg.asInstanceOf[String])
  }
}
