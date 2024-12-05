package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._
import scala.concurrent.duration._
import scala.language.postfixOps

class LocalSimulation extends BaseSimulation {

  // Define test data feeds
  val userCredentials = csv("users.csv").random
  val contentIds = csv("content.csv").random

  // HTTP Protocol specific
  override val httpProtocol = http
    .baseUrl(getEnvConfig("BASE_URL", "https://qa.com"))
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Performance Test")
    .header("Origin", "https://qa.com")
    .header("Referer", "https://qa.com/")

  // Integrated Scenario Workflows
  val integratedUserJourney = scenario("User Complete Journey")
    .feed(userCredentials)
    .feed(contentIds)
    .exec(new AuthenticationScenario().authScenario)
    .pause(2, 5)
    .exec(new HomepageScenario().homepageScenario)
    .pause(3, 7)
    .exec(new StreamingScenario().streamingScenario)

  // Define load injection strategies
  setUp(
    integratedUserJourney.inject(
      rampUsers(5).during(3.seconds),
      constantUsersPerSec(2).during(1.minutes)
    )
  )
    .protocols(httpProtocol)
    .maxDuration(1.minutes)
    .assertions(
      global.responseTime.max.lte(5000),
      global.successfulRequests.percent.gte(99),
      global.failedRequests.count.lte(5)
    )
}