package com.lunatech.airports.messagespec

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.json4s.{Formats, NoTypeHints}
import org.json4s.native.Serialization

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by zandrewitte on 2017/07/12.
  * MessageSpec
  */

case class Airport(id: Long, ident: String, `type`: String, name: String, latitudeDeg: Double, longitudeDeg: Double, elevationFeet: Option[Long],
                   continent: String, isoCountry: String, isoRegion: String, municipality: Option[String], scheduledService: Option[String],
                   gpsCode: Option[String], iataCode: Option[String], localCode: Option[String], homeLink: Option[String], wikipediaLink: Option[String], keywords: List[String])

case class Country(id: Long, code: String, name: String, continent: String, wikipediaLink: String, keywords: List[String])

case class Runway(id: Long, airportRef: Long, airportIdent: String, lengthFt: Option[Long], widthFt: Option[Long], surface: Option[String],
                  lighted: String, closed: String, leIdent: Option[String], leLatitudeDeg: Option[Double],leLongitudeDeg: Option[Double],
                  leElevationFt: Option[Long], leHeadingDegT: Option[Double], leDisplacedThresholdFt: Option[Long], heIdent: Option[String],
                  heLatitudeDeg: Option[Double], heLongitudeDeg: Option[Double], heElevationFt: Option[Long], heHeadingDegT: Option[Double],
                  heDisplacedThresholdFt: Option[Long])

object Implicits {

  implicit val system = ActorSystem("AirportsSystem")
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(10 seconds)
  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  implicit class Map2Class(values: Map[String,String]){
    def convert[A](implicit mapper: MapConvert[A]): A = mapper conv values
  }

  trait MapConvert[A]{
    def conv(values: Map[String,String]): A
  }

  implicit val mapAirport = new MapConvert[Airport]{
    def conv(map: Map[String, String]) = Airport(map("id").toLong, map("ident"), map("type"), map("name"), map("latitude_deg").toDouble, map("longitude_deg").toDouble,
      map.get("elevation_ft").map(_.toLong), map("continent"), map("iso_country"), map("iso_region"), map.get("municipality"), map.get("scheduled_service"),
      map.get("gps_code"), map.get("iata_code"), map.get("local_code"), map.get("home_link"), map.get("wikipedia_link"),
      map.get("keywords").fold(List[String]())(_.split(",").toList))
  }

  implicit val mapCountry = new MapConvert[Country]{
    def conv(map: Map[String, String]) = Country(map("id").toLong, map("code"), map("name"), map("continent"), map("wikipedia_link"), map.get("keywords").fold(List[String]())(_.split(",").toList))
  }

  implicit val mapRunway = new MapConvert[Runway]{
    def conv(map: Map[String, String]) = Runway(map("id").toLong, map("airport_ref").toLong, map("airport_ident"), map.get("length_ft").map(_.toLong), map.get("width_ft").map(_.toLong),
      map.get("surface"), map("lighted"), map("closed"), map.get("le_ident"), map.get("le_latitude_deg").map(_.toDouble), map.get("le_longitude_deg").map(_.toDouble),
      map.get("le_elevation_ft").map(_.toLong), map.get("le_heading_degT").map(_.toDouble), map.get("le_displaced_threshold_ft").map(_.toLong), map.get("he_ident"), map.get("he_latitude_deg").map(_.toDouble),
      map.get("he_longitude_deg").map(_.toDouble), map.get("he_elevation_ft").map(_.toLong), map.get("he_heading_degT").map(_.toDouble), map.get("he_displaced_threshold_ft").map(_.toLong))
  }

}