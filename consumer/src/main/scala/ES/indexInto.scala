package ES

import Pokemon.Pokemon
import com.sksamuel.elastic4s.ElasticDsl._

import scala.concurrent.ExecutionContext

object IndexInto {

  def pokemon(pokemon: Pokemon)(implicit ec: ExecutionContext) = {

    ESClient.c.execute {
      index into "pokemons" -> "pokemon" fields Map(
        "id" -> pokemon.id,
        "name" -> pokemon.name.getOrElse("Unknown"),
        "geo" -> Map(
          "lat" -> pokemon.geo.lat,
          "lon" -> pokemon.geo.lon
        ),
        "expireAt" -> pokemon.expireUTC
      )
    }
  }
}
