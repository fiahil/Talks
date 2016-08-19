package Pokemon

import org.joda.time.format.ISODateTimeFormat
import play.api.libs.functional.syntax._
import play.api.libs.json._


case class Pokemon(id: Int,
                   geo: LatLon,
                   name: Option[String],
                   expireAt: String) {

  val expireUTC = ISODateTimeFormat.dateTime().parseDateTime(expireAt + "+00:00")
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
}
