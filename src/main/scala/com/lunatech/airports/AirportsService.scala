package com.lunatech.airports

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import com.lunatech.airports.routes._

import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by zandrewitte on 2017/07/05.
  * AirportsService
  */
object AirportsService extends App{
  import com.lunatech.airports.messagespec.Implicits._

  val routes = pathPrefix("airport-api" / "v1") { respondWithHeaders(
    RawHeader("Access-Control-Allow-Origin", "http://localhost:8081"),
    RawHeader("Access-Control-Allow-Credentials", "true")
  ) {
    AirportRoutes.routes ~ CountryRoutes.routes ~ RunwayRoutes.routes
  }}

  val routeBinding = Http().bindAndHandle(routes, "localhost", 8090)
  println(s"Server online at http://localhost:8090/\nPress RETURN to stop...")

}

/*
2. Write a web application in Java or Scala that will ask the user for two actions : Query or Reports.

2.1 Query Option will ask the user for the country name or code and print the airports & runways at each airport. The input can be country code or country name. For bonus points make the test partial/fuzzy. e.g. entering zimb will result in Zimbabwe :)

2.2 Choosing Reports will print the following:

10 countries with highest number of airports (with count) and countries with lowest number of airports.
Type of runways (as indicated in "surface" column) per country
Bonus: Print the top 10 most common runway identifications (indicated in "le_ident" column)
Feel free to use any library/framework as necessary but write it as a web application.

Please write the code as if you are writing production code, possibly with tests.
 */






