package services.ES

import com.sksamuel.elastic4s.ElasticDsl._

import ESClient.ESContext

object DeleteFrom {

  def pokemon(id: String) = {

    ESClient.c.execute {

      // unindex expired pokemons from elasticsearch
      delete id id from "pokemons" / "pokemon"
    } onFailure { case ex => println(ex) }
  }
}
