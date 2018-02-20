package com.zantech.airports.messagespec

/**
  * Created by zandrewitte on 2017/07/12.
  * RunwayMessageSpec
  */
case class GetRunwaysByAirport(airportIdentifier: String)

case class GetRunwayTypesByCountries()

case class RunwayTypeCount(runwayType: String, count: Int)
case class CountryRunwayTypes(countryName: String, runwayTypes: Vector[RunwayTypeCount])

case class GetRunwayIdentifications()
case class RunwayIdentifications(runwayIdentification: String, count: Int)