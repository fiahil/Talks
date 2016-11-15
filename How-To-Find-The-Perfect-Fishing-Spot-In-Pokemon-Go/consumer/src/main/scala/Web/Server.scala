package web

import java.util.concurrent.Executors

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import services.ES.SearchIn
import spray.can.Http
import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes._
import spray.routing._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object Server {

  // we don't implement our route structure directly in the service actor because
  // we want to be able to test it independently, without having to spin up an actor
  class myServer extends Actor with HttpService {

    // Backref to our actorsystem
    implicit val system = context.system

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

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))

  // create and start our service actor
  val service = system.actorOf(Props[myServer], "webserver")

  implicit val timeout = Timeout(5.seconds)


  def start = {
    // start a new HTTP server on port 8080 with our service actor as the handler
    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8081)
  }
}
