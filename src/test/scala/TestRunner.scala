import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.routing.FromConfig
import akka.testkit.{ImplicitSender, TestActorRef, TestActors, TestKit, TestProbe}
import org.scalatest._
import com.lunatech.airports.actors.{AirportActor, CountryActor, RunwayActor}
import com.lunatech.airports.messagespec._
import com.lunatech.airports.messagespec.Implicits.{system, _}

/**
  * Created by zandrewitte on 2017/07/14.
  * TestRunner
  */
class TestRunner extends AsyncFlatSpec with Matchers with BeforeAndAfterAll {

  val airportActor: ActorRef = system.actorOf(FromConfig.props(Props[AirportActor]), "airportActor")
  val countryActor: ActorRef = system.actorOf(FromConfig.props(Props[CountryActor]), "countryActor")
  val runwayActor: ActorRef = system.actorOf(FromConfig.props(Props[RunwayActor]), "runwayActor")

  "The Country Actor " should " return a country for the corresponding ISO Country Code" in {
    (countryActor ? GetCountryByQuery("US")).mapTo[Option[Country]].map(countryOpt =>
      assert(countryOpt.fold(false)(_.name === "United States"))
    )
  }

  "The Airport Actor " should " return a list of airports for the corresponding Country Code " in {
    (airportActor ? GetAirportsByCountry("US")).mapTo[List[Airport]].map(airportList =>
      assert(airportList.forall(_.isoCountry === "US"))
    )
  }

  "The Runway Actor " should " return a list of runway for the corresponding Airport Identifier " in {
    (runwayActor ? GetRunwaysByAirport("00CO")).mapTo[List[Runway]].map(runwayList =>
      assert(runwayList.forall(_.airportIdent === "00CO"))
    )
  }

  override def afterAll {
    system.terminate()
  }

}