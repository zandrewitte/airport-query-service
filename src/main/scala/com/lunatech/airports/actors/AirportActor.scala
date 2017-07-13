package com.lunatech.airports.actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import com.lunatech.airports.CsvFileStreams._
import com.lunatech.airports.messagespec._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz._
import Scalaz._

/**
  * Created by zandrewitte on 2017/07/12.
  * AirportActor
  */
class AirportActor extends Actor with ActorLogging {

  def receive: Receive = {
    case GetAirportsByCountry(countryCode) =>
      log.info(s"Finding Airports for Country: $countryCode")
      airportsStream.map(_.filter(_.isoCountry === countryCode)) pipeTo sender()
    case GetAirportsByCountries() =>
      log.info(s"Finding Top/Bottom 10 Countries")
      airportsStream.flatMap(airports => {
        val airportCount = airports.groupBy(_.isoCountry)
            .map{case (countryCode, countryAirports) => CountryAirport(countryCode, countryAirports.size)}
            .toList.sortBy(_.airportCount)(scala.Ordering.Int.reverse)

        val countryAirportList = airportCount.take(10) ::: airportCount.takeRight(10)
        val countryCodeList =  countryAirportList.map(_.countryCode)

        countryStream.map{countryList =>
          val countryNames = countryList.filter(country => countryCodeList.contains(country.code)).groupBy(_.code)
          val topAirports = countryAirportList.map(countryAirport => CountryAirport(countryNames(countryAirport.countryCode).head.name, countryAirport.airportCount))

          CountryAirportSummary(topAirports.take(10), topAirports.takeRight(10))
        }

      }) pipeTo sender()

  }

}
