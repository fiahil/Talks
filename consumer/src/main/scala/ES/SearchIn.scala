package ES

import Pokemon.Pokemon
import com.sksamuel.elastic4s.ElasticDsl._
import play.api.libs.json.Json
import scala.concurrent.Future

import ESClient.ESExecutionContext

object SearchIn {

  def pokemon: Future[String] = {
    ESClient.c.execute {
      search in "pokemons" / "pokemon" query matchAllQuery limit 20
    } map { sr =>
      Json.stringify {
        Json.toJson {
          println(sr)
          sr.as[Pokemon]
          }
        }
    }
  }
}
