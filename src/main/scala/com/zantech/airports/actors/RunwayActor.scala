package com.zantech.airports.actors

import akka.actor.{Actor, ActorLogging}
import com.zantech.airports.messagespec._
import com.zantech.airports.messagespec.Implicits._
import com.zantech.airports.IngestStreams._
import org.apache.spark.sql.functions._
/**
  * Created by zandrewitte on 2017/07/12.
  * RunwayActor
  */
class RunwayActor extends Actor with ActorLogging {
  import spark.implicits._

  def receive: Receive = {
    case GetRunwaysByAirport(airportIdentifier) =>
      log.info(s"Finding Runways for Airport: $airportIdentifier")
      sender() ! runways.filter($"airport_ident" === airportIdentifier).toJSON.collect().toVector

    case GetRunwayIdentifications() =>
      log.info(s"Finding Runway Identifications")
      sender() ! runways.groupBy("le_ident")
        .count()
        .sort(desc("count"))
        .withColumnRenamed("le_ident", "runwayIdentification")
        .cache()
        .toJSON
        .collect()
        .toVector

    case GetRunwayTypesByCountries() =>
      log.info(s"Finding Runways Types for Countries")


//        .groupBy(Int.MaxValue, _.code)

//        .flatMapConcat(country => airportsStream.filter(_.isoCountry === country.code))
//        .flatMapConcat(airport => runwaysStream.filter(_.airportIdent === airport.ident))
//        .groupBy(Int.MaxValue, _.surface.getOrElse("unknown"))
//        .fold(RunwayTypeCount("", 0))((acc, runway) =>
//          acc.copy(
//            runwayType = runway.surface.getOrElse("unknown"),
//            count = acc.count + 1
//          )
//        )
//        .mergeSubstreams
//        .mergeSubstreams
//        .runForeach(println)
//        .runWith(Sink.seq) pipeTo sender()




//      runwaysStream
//        .groupBy(Int.MaxValue, _.airportIdent)
//        .flatMapMerge(Int.MaxValue, runway => )

//      (for {
//        runwayMap <- runwaysStream.map(_.groupBy(_.airportIdent))
//        airportMap <- airportsStream.map(_.map(airport => (airport.isoCountry, runwayMap.getOrElse(airport.ident, List()))).groupBy(_._1))
//        countryList <- countryStream.map(_.map(country => CountryRunwayTypes(country.name, airportMap.getOrElse(country.code, List())
//          .flatMap(_._2).groupBy(_.surface.getOrElse("unknown")).toList
//          .map(grpAirport => RunwayTypeCount(grpAirport._1, grpAirport._2.size))
//          .sortBy(_.count)(scala.Ordering.Int.reverse))))
//      } yield countryList.sortBy(_.countryName)) pipeTo sender()

  }

}