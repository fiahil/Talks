package services.ES

import models.Pokemon
import com.sksamuel.elastic4s.ElasticDsl._
import play.api.libs.json.Json
import scala.concurrent.Future

import ESClient.ESContext

object SearchIn {

  def pokemon: Future[String] = {
    ESClient.c.execute {

      // Search for all pokemons on the map
      search in "pokemons" / "pokemon" query matchAllQuery limit 200
    } map { sr =>
      Json.stringify {
        Json.toJson {
          sr.as[Pokemon]
          }
        }
    }
  }
}
