package com.lunatech.airports

import java.nio.file.Paths

import akka.NotUsed
import akka.stream.scaladsl.{FileIO, Flow, Framing}
import akka.util.ByteString
import com.lunatech.airports.messagespec.{Airport, Country, Runway}

import scala.concurrent.Future

/**
  * Created by zandrewitte on 2017/07/12.
  * CsvFileStreams
  */
object CsvFileStreams {
  import com.lunatech.airports.messagespec.Implicits._

  def rowConverterFlow[A](implicit mapConvert: MapConvert[A]): Flow[ByteString, A, NotUsed] = Flow[ByteString]
    .map(_.utf8String.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1).toList.map{_.replace("\"", "")})
    .prefixAndTail(1).flatMapConcat { case (headers, rows) =>
    rows.map { row => headers.head.zip(row).filter(_._2.nonEmpty).toMap.convert[A] }
  }

  val airportsStream: Future[List[Airport]] = FileIO.fromPath(Paths.get("./src/main/resources/airports.csv"))
    .via(Framing.delimiter(ByteString(System.lineSeparator), maximumFrameLength = 4096, allowTruncation = true))
    .via(rowConverterFlow[Airport](mapAirport))
    .runFold(List[Airport]())((v1, v2) => v2 :: v1)

  val countryStream: Future[List[Country]] = FileIO.fromPath(Paths.get("./src/main/resources/countries.csv"))
    .via(Framing.delimiter(ByteString(System.lineSeparator), maximumFrameLength = 4096, allowTruncation = true))
    .via(rowConverterFlow[Country](mapCountry))
    .runFold(List[Country]())((v1, v2) => v2 :: v1)

  val runwaysStream: Future[List[Runway]] = FileIO.fromPath(Paths.get("./src/main/resources/runways.csv"))
    .via(Framing.delimiter(ByteString(System.lineSeparator), maximumFrameLength = 4096, allowTruncation = true))
    .via(rowConverterFlow[Runway](mapRunway))
    .runFold(List[Runway]())((v1, v2) => v2 :: v1)

}
