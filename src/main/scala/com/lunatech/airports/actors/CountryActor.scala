package com.lunatech.airports.actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import com.lunatech.airports.CsvFileStreams._
import com.lunatech.airports.messagespec.{Country, GetCountryByQuery}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz._, Scalaz._
import scala.math.min

import scala.language.postfixOps

/**
  * Created by zandrewitte on 2017/07/12.
  * CountryActor
  */
class CountryActor extends Actor with ActorLogging {

  def receive: Receive = {
    case GetCountryByQuery(query) =>
      log.info(s"Finding Country for Query: $query")
      countryStream.map(countryList =>
        countryList.find(_.code === query.toUpperCase).orElse(
          countryList.filter(_.name.toUpperCase.contains(query.toUpperCase))
            .sortWith((country1, country2) => editDist(country1.name, query) < editDist(country2.name, query)).headOption
        )
      ) pipeTo sender()
  }

  def editDist[A](a: Iterable[A], b: Iterable[A]): Int =
    ((0 to b.size).toList /: a)((prev, x) =>
      (prev zip prev.tail zip b).scanLeft(prev.head + 1) {
        case (h, ((d, v), y)) => min(min(h + 1, v + 1), d + (if (x == y) 0 else 1))
      }) last

}