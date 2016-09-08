package ES

import Pokemon.Pokemon
import com.sksamuel.elastic4s.ElasticDsl._

import ESClient.ESExecutionContext

object IndexInto {

  def pokemon(pokemon: Pokemon) = {

    ESClient.c.execute {
      index into "pokemons" -> "pokemon" source pokemon
    } onFailure { case ex => println(ex) }
  }
}
