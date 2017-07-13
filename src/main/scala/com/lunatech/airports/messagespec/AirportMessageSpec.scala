package com.lunatech.airports.messagespec

/**
  * Created by zandrewitte on 2017/07/12.
  * AirportMessageSpec
  */
case class GetAirportsByCountry(countryCode: String)
case class GetAirportsByCountries()

case class CountryAirport(countryCode: String, airportCount: Int)

case class CountryAirportSummary(topAirportCountries: List[CountryAirport], bottomAirportCountries: List[CountryAirport])