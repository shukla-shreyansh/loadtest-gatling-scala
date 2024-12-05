package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class StreamingScenario {

  val streamingScenario = exec(
    http("Start Media Stream")
      .get("/shows/maddam-sir-1700000181/the-bomb-man-1000062810?watch=true")
      .check(status.is(200))
      .check(responseTimeInMillis.lte(2000))
  )
    .pause(5)
    .exec(
      http("Pause Stream")
        .post("/shows/maddam-sir-1700000181/the-bomb-man-1000062810?watch=true")
        .check(status.is(200))
    )
    .pause(2)
    .exec(
      http("Resume Stream")
        .post("/shows/maddam-sir-1700000181/the-bomb-man-1000062810?watch=true")
        .check(status.is(200))
    )
    .pause(10)
    .exec(
      http("Stop Stream")
        .post("/shows/maddam-sir-1700000181/the-bomb-man-1000062810?watch=true")
        .check(status.is(200))
    )
}
