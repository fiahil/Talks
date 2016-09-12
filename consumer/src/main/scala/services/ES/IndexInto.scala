package services.ES

import models.Pokemon
import com.sksamuel.elastic4s.ElasticDsl._

import ESClient.ESContext

object IndexInto {

  def pokemon(pokemon: Pokemon) = {

    ESClient.c.execute {
      index into "pokemons" -> "pokemon" source pokemon
    } onFailure { case ex => println(ex) }
  }
}
