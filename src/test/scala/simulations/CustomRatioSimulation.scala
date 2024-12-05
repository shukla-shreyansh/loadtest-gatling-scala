package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CustomRatioSimulation extends Simulation {

  // Base URL
  val httpProtocol = http
    .baseUrl("https://example.com") // Replace with your base URL
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // Ratios for different user journeys
  val browseWeight = 50.0
  val loginWeight = 20.0
  val streamWeight = 30.0

  // Combined Scenario with Ratios
  val combinedScenario = scenario("User Journey with Custom Ratios")
    .randomSwitch(
//      browseWeight -> exec(browseScenario),
//      loginWeight -> exec(loginScenario),
//      streamWeight -> exec(streamScenario)
    )

  setUp(
    combinedScenario.inject(
      atOnceUsers(10),            // Start with 10 users instantly
      rampUsers(100).during(10),  // Ramp up to 100 users over 10 seconds
      constantUsersPerSec(20).during(60.seconds) // Sustain 20 users per second for 1 minute
    )
  ).protocols(httpProtocol)
}
