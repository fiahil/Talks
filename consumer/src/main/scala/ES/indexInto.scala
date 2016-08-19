package ES

import Pokemon.Pokemon
import com.sksamuel.elastic4s.ElasticDsl._

import scala.concurrent.ExecutionContext

object IndexInto {

  def pokemon(pokemon: Pokemon)(implicit ec: ExecutionContext) = {

    ESClient.c.execute {
      index into "pokemons" -> "pokemon" source pokemon
    } onFailure { case ex => println(ex) }
  }
}
