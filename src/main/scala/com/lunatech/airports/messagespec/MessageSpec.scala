package com.lunatech.airports.messagespec

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.apache.spark.sql.SparkSession

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by zandrewitte on 2017/07/12.
  * MessageSpec
  */

object Namespace {
  final val Airports = "airports"
  final val Countries = "countries"
  final val Runways = "runways"
}
object TableName {
  final val Airports = "global_temp.airports"
  final val Countries = "global_temp.countries"
  final val Runways = "global_temp.runways"
}

object Implicits {

  implicit val system: ActorSystem = ActorSystem("AirportsSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val timeout: Timeout = Timeout(10 seconds)

  val spark: SparkSession = org.apache.spark.sql.SparkSession.builder
    .master("local")
    .appName("Spark CSV Reader")
    .getOrCreate

}