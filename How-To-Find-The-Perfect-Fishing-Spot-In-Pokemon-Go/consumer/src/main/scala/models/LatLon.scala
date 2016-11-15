package models

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class LatLon(lat: Double, lon: Double)

object LatLon {

  /**
   * Serialize a lat-lon object to/from JSON
   */
  implicit val format: Format[LatLon] = (
                                          (__ \ 'lat).format[Double] and
                                          (__ \ 'lon).format[Double]
                                          )(LatLon.apply, unlift(LatLon.unapply))
}
