package Pokemon

import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s.{HitAs, RichSearchHit}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.functional.syntax._
import play.api.libs.json._


case class Pokemon(id: Int,
                   geo: LatLon,
                   name: Option[String],
                   expireAt: String) {

  val expireUTC = DateTime.parse(expireAt)
}


object Pokemon {

  implicit val pokemonFormat: Format[Pokemon] = (
                                                  (__ \ 'id).format[Int] and
                                                  (__ \ 'geo).format[LatLon] and
                                                  (__ \ 'name).formatNullable[String] and
                                                  (__ \ 'expireAt).format[String]
                                                  )(Pokemon.apply, unlift(Pokemon.unapply))

  def fromMessage(message: Array[Byte]): Option[Pokemon] = {
    Json.parse(message).validate[Pokemon].asOpt
  }

  implicit object PokemonHitAs extends HitAs[Pokemon] {

    override def as(hit: RichSearchHit): Pokemon = {
      Json.parse(hit.sourceAsString).validate[Pokemon].asOpt.get
    }
  }

  implicit object PokemonJson extends Indexable[Pokemon] {

    override def json(t: Pokemon): String = Json.stringify(Json.toJson(t))
  }
}
