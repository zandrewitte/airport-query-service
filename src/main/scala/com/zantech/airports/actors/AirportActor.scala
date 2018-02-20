package com.zantech.airports.actors

import akka.actor.{Actor, ActorLogging}
import com.zantech.airports.messagespec._
import com.zantech.airports.messagespec.Implicits._
import com.zantech.airports.IngestStreams._
import org.apache.spark.sql.functions._
/**
  * Created by zandrewitte on 2017/07/12.
  * AirportActor
  */
class AirportActor extends Actor with ActorLogging {
  import spark.implicits._

  def receive: Receive = {
    case GetAirportsByCountry(countryCode) =>
      log.info(s"Finding Airports for Country: $countryCode")
      sender() ! airports.filter($"iso_country" === countryCode).toJSON.collect().toVector

    case GetAirportsByCountries() =>
      log.info(s"Finding Top/Bottom 10 Countries")

      val grouped = airports.select($"name", $"iso_country")
        .as('airport)
        .join(countries.as('country), $"airport.iso_country" === $"country.code")
        .groupBy($"country.name")
        .count()
        .cache()

      sender() ! s"""{
         |"topAirportCountries": [${grouped.sort(desc("count")).limit(10).toJSON.cache().collect().toVector.mkString(",")}],
         |"bottomAirportCountries": [${grouped.sort(asc("count")).limit(10).toJSON.cache().collect().toVector.mkString(",")}]
       }""".stripMargin

  }

}
