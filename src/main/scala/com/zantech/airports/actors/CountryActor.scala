package com.zantech.airports.actors

import akka.actor.{Actor, ActorLogging}
import com.zantech.airports.messagespec.{GetCountryByQuery, TableName}
import com.zantech.airports.messagespec.Implicits._
import com.zantech.airports.IngestStreams._
import scala.language.postfixOps
import org.apache.spark.sql.functions._

/**
  * Created by zandrewitte on 2017/07/12.
  * CountryActor
  */
class CountryActor extends Actor with ActorLogging {
  import spark.implicits._

  def receive: Receive = {
    case GetCountryByQuery(query) =>
      log.info(s"Finding Country for Query: $query")

      val directMatch = countries.filter($"code" === query).limit(1).toJSON.collect().toVector

      val res = if(directMatch.isEmpty)
        countries.withColumn("name_query_levenshtein", levenshtein($"name", lit(query)))
          .cache()
          .sort($"name_query_levenshtein")
          .limit(1)
          .toJSON
          .collect()
          .headOption
      else directMatch.headOption

      sender() ! res

  }

}