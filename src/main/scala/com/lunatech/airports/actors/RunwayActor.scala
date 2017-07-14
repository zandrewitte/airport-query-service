package com.lunatech.airports.actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import com.lunatech.airports.CsvFileStreams._
import com.lunatech.airports.messagespec._

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz._
import Scalaz._
/**
  * Created by zandrewitte on 2017/07/12.
  * RunwayActor
  */
class RunwayActor extends Actor with ActorLogging {

  def receive: Receive = {
    case GetRunwaysByAirport(airportIdentifier) =>
      log.info(s"Finding Runways for Airport: $airportIdentifier")
      runwaysStream.map(_.filter(_.airportIdent === airportIdentifier)) pipeTo sender()

    case GetRunwayIdentifications() =>
      log.info(s"Finding Runway Identifications")
      runwaysStream.map(_.groupBy(_.leIdent.getOrElse("unknown")).toList
        .map(grpRunway => RunwayIdentifications(grpRunway._1, grpRunway._2.size))) pipeTo sender()

    case GetRunwayTypesByCountries() =>
      log.info(s"Finding Runways Types for Countries")

      (for {
        runwayMap <- runwaysStream.map(_.groupBy(_.airportIdent))
        airportMap <- airportsStream.map(_.map(airport => (airport.isoCountry, runwayMap.getOrElse(airport.ident, List()))).groupBy(_._1))
        countryList <- countryStream.map(_.map(country => CountryRunwayTypes(country.name, airportMap.getOrElse(country.code, List())
          .flatMap(_._2).groupBy(_.surface.getOrElse("unknown")).toList
          .map(grpAirport => RunwayTypeCount(grpAirport._1, grpAirport._2.size))
          .sortBy(_.count)(scala.Ordering.Int.reverse))))
      } yield countryList.sortBy(_.countryName)) pipeTo sender()

  }

}