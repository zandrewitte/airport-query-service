package com.lunatech.airports.routes

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.routing.FromConfig

import com.lunatech.airports.actors.CountryActor
import com.lunatech.airports.messagespec.{Country, GetCountryByQuery}

import org.json4s.native.Serialization.{read, write}

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by zandrewitte on 2017/07/12.
  * CountryRoutes
  */
object CountryRoutes {
  import com.lunatech.airports.messagespec.Implicits._

  val countryActor: ActorRef = system.actorOf(FromConfig.props(Props[CountryActor]), "countryActor")

  val routes: Route =
    path("country") {
      get {
        parameter('query) { query =>
          onComplete((countryActor ? GetCountryByQuery(query)).mapTo[Option[Country]]) {
            case Success(countryOpt) =>
              countryOpt.fold(
                complete(StatusCodes.NoContent)
              )(country =>
                complete(HttpEntity(ContentTypes.`application/json`, write(country)))
              )
            case Failure(ex) => complete(StatusCodes.InternalServerError, s"""{error: "${ex.getMessage}"}""")
          }
        }
      }
    }

}
