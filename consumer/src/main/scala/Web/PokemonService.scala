package Web

import ES.SearchIn
import akka.actor.Actor
import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes._
import spray.routing._
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class PokemonService extends Actor with HttpService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)


  val myRoute = {
    path("api" / "pokemons") {
      get {
        respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
          respondWithMediaType(`application/json`) {
            onComplete[String](SearchIn.pokemon) {
              case Success(value) => complete(200, value)
              case Failure(ex)    => complete(500, ex.getMessage)
            }
          }
        }
      }
    }
  }
}
