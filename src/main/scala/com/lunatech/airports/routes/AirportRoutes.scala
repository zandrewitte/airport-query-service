package com.lunatech.airports.routes

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.routing.FromConfig
import org.json4s.native.Serialization.{read, write}
import com.lunatech.airports.actors.AirportActor
import com.lunatech.airports.messagespec._

import scala.util.{Failure, Success}
/**
  * Created by zandrewitte on 2017/07/12.
  * AirportRoutes
  */
object AirportRoutes {
  import com.lunatech.airports.messagespec.Implicits._

  val airportActor: ActorRef = system.actorOf(FromConfig.props(Props[AirportActor]), "airportActor")

  val routes: Route =
    path("airports" / "reports" / "byCountries") {
      get {
        onComplete((airportActor ? GetAirportsByCountries()).mapTo[CountryAirportSummary]) {
          case Success(airports) => complete(HttpEntity(ContentTypes.`application/json`, write(airports)))
          case Failure(ex) => complete(StatusCodes.InternalServerError, s"""{error: "${ex.getMessage}"}""")
        }
      }
    } ~
    path(Segment / "airports") { countryCode =>
      get {
        onComplete((airportActor ? GetAirportsByCountry(countryCode)).mapTo[List[Airport]]) {
          case Success(airports) => complete(HttpEntity(ContentTypes.`application/json`, write(airports)))
          case Failure(ex) => complete(StatusCodes.InternalServerError, s"""{error: "${ex.getMessage}"}""")
        }
      }
    }

}
