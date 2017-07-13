package com.lunatech.airports.routes

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.routing.FromConfig
import org.json4s.native.Serialization.{read, write}
import com.lunatech.airports.actors.RunwayActor
import com.lunatech.airports.messagespec._

import scala.util.{Failure, Success}
/**
  * Created by zandrewitte on 2017/07/12.
  * RunwayRoutes
  */
object RunwayRoutes {
  import com.lunatech.airports.messagespec.Implicits._

  val runwayActor: ActorRef = system.actorOf(FromConfig.props(Props[RunwayActor]), "runwayActor")

  val routes: Route =
    pathPrefix("runways" / "reports") {
      path("surfaceByCountries") {
        get {
          onComplete((runwayActor ? GetRunwayTypesByCountries()).mapTo[List[CountryRunwayTypes]]) {
            case Success(airports) => complete(HttpEntity(ContentTypes.`application/json`, write(airports)))
            case Failure(ex) => complete(StatusCodes.InternalServerError, s"""{error: "${ex.getMessage}"}""")
          }
        }
      } ~
      path("runwayIdentifications") {
        get {
          onComplete((runwayActor ? GetRunwayIdentifications()).mapTo[List[RunwayIdentifications]]) {
            case Success(airports) => complete(HttpEntity(ContentTypes.`application/json`, write(airports)))
            case Failure(ex) => complete(StatusCodes.InternalServerError, s"""{error: "${ex.getMessage}"}""")
          }
        }
      }
    } ~
    path(Segment / "runways") { airportIdentifier =>
      get {
        onComplete((runwayActor ? GetRunwaysByAirport(airportIdentifier)).mapTo[List[Runway]]) {
          case Success(runways) => complete(HttpEntity(ContentTypes.`application/json`, write(runways)))
          case Failure(ex) => complete(StatusCodes.InternalServerError, s"""{error: "${ex.getMessage}"}""")
        }
      }
    }

}
