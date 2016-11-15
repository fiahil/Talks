package services.ES

import models.Pokemon
import com.sksamuel.elastic4s.ElasticDsl._

import ESClient.ESContext

object IndexInto {

  def pokemon(pokemon: Pokemon) = {

    ESClient.c.execute {

      // index new pokemons into elasticsearch
      index into "pokemons" -> "pokemon" source pokemon
    } map { response => response.id }
  }
}
