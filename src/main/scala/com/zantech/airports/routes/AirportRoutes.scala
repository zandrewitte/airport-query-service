package com.zantech.airports.routes

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.routing.FromConfig
import com.zantech.airports.actors.AirportActor
import com.zantech.airports.messagespec._
import com.zantech.airports.messagespec.Implicits._

import scala.util.{Failure, Success}
/**
  * Created by zandrewitte on 2017/07/12.
  * AirportRoutes
  */
object AirportRoutes {

  val airportActor: ActorRef = system.actorOf(FromConfig.props(Props[AirportActor]), "airportActor")

  val routes: Route =
    path("airports" / "reports" / "byCountries") {
      get {
        onComplete((airportActor ? GetAirportsByCountries()).mapTo[String]) {
          case Success(result) => complete(HttpEntity(ContentTypes.`application/json`, result))
          case Failure(ex) => complete(StatusCodes.InternalServerError, s"""{error: "${ex.getMessage}"}""")
        }
      }
    } ~
    path(Segment / "airports") { countryCode =>
      get {
        onComplete((airportActor ? GetAirportsByCountry(countryCode)).mapTo[Vector[String]]) {
          case Success(result) => complete(HttpEntity(ContentTypes.`application/json`, s"[${result.mkString(",")}]"))
          case Failure(ex) => complete(StatusCodes.InternalServerError, s"""{error: "${ex.getMessage}"}""")
        }
      }
    }

}
