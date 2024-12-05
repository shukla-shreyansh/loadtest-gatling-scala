package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class HomepageScenario {
  val homepageScenario = scenario("Homepage Scenario")
    .exec(
      http("Load Homepage")
        .get("/")
        .check(status.is(200))
        .check(regex("Welcome").exists)
    )
    .pause(2)
    .exec(
      http("Browse Featured Content")
        .get("/featured")
        .queryParam("category", "movies")
        .check(status.is(200))
        .check(jsonPath("$.items.length()").ofType[Int].gt(0))
    )
}
