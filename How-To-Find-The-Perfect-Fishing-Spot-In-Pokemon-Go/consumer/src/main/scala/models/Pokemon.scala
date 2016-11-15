package models

import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s.{HitAs, RichSearchHit}
import com.github.nscala_time.time.Imports._
import play.api.libs.functional.syntax._
import play.api.libs.json._


case class Pokemon(id: Int, geo: LatLon, expireAt: DateTime)


object Pokemon {

  implicit val dateRead = Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
  implicit val dateWrite = Writes.DefaultJodaDateWrites
  implicit val dateFormat = Format.apply(dateRead, dateWrite)

  /**
   * Serialize Pokemon to/from JSON
   */
  implicit val format = (
                          (__ \ 'id).format[Int] and
                          (__ \ 'geo).format[LatLon] and
                          (__ \ 'expireAt).format[DateTime](dateFormat)
                          )(Pokemon.apply, unlift(Pokemon.unapply))


  /**
   * Read a message and transform it into a Pokemon
   */
  def fromMessage(message: Array[Byte]): Option[Pokemon] = {
    Json.parse(message).validate[Pokemon].asOpt
  }

  /**
   * Serialize services.ES search hits to Pokemons
   */
  implicit object PokemonHitAs extends HitAs[Pokemon] {

    override def as(hit: RichSearchHit): Pokemon = {
      Json.parse(hit.sourceAsString).validate[Pokemon].asOpt.get
    }
  }

  /**
   * Serialize Pokemons to indexable documents (services.ES)
   * using our json formatter defined above
   */
  implicit object PokemonJson extends Indexable[Pokemon] {

    override def json(t: Pokemon): String = Json.stringify(Json.toJson(t))
  }

}
