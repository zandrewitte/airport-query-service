package com.zantech.airports

import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives.{pathPrefix, respondWithHeaders}
import com.zantech.airports.routes.{AirportRoutes, CountryRoutes, RunwayRoutes}
import com.zantech.airports.messagespec.Implicits._

/**
  * Created by zandrewitte on 2017/07/05.
  * AirportsService
  */
object AirportsService extends App {

  IngestStreams.read()

  val routes = pathPrefix("airport-api" / "v1") { respondWithHeaders(
    RawHeader("Access-Control-Allow-Origin", "http://localhost:8081"),
    RawHeader("Access-Control-Allow-Credentials", "true")
  ) {
    AirportRoutes.routes ~ CountryRoutes.routes ~ RunwayRoutes.routes
  }}

  val routeBinding = Http().bindAndHandle(routes, "localhost", 8090)
  println(s"Server online at http://localhost:8090/\nPress RETURN to stop...")

}
