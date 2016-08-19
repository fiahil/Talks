package Pokemon

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class LatLon(lat: Double, lon: Double)

object LatLon {

  implicit val LatLngFormat: Format[LatLon] = (
                                                (__ \ 'lat).format[Double] and
                                                (__ \ 'lon).format[Double]
                                                )(LatLon.apply, unlift(LatLon.unapply))
}
