package com.lunatech.airports

import java.nio.file.Paths

import com.lunatech.airports.messagespec.Implicits.spark
import com.lunatech.airports.messagespec.{Namespace, TableName}
import org.apache.spark.sql.DataFrame

object IngestStreams {

  def read(): Unit = {

    spark.read
      .format("csv")
      .option("header", "true") //reading the headers
      .load(Paths.get("./src/main/resources/airports.csv").toString)
      .createOrReplaceGlobalTempView(Namespace.Airports)

    spark.read
      .format("csv")
      .option("header", "true") //reading the headers
      .load(Paths.get("./src/main/resources/countries.csv").toString)
      .createOrReplaceGlobalTempView(Namespace.Countries)

    spark.read
      .format("csv")
      .option("header", "true") //reading the headers
      .load(Paths.get("./src/main/resources/runways.csv").toString)
      .createOrReplaceGlobalTempView(Namespace.Runways)

  }

  lazy val airports: DataFrame = spark.table(TableName.Airports)
  lazy val countries: DataFrame = spark.table(TableName.Countries)
  lazy val runways: DataFrame = spark.table(TableName.Runways)

}
