package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class AuthenticationScenario {
  val authScenario = exec(
    http("Login Request")
      .post("/login")
      .body(StringBody("""{"username": "${email}", "password": "${password}"}"""))
      .check(status.is(200))
      .check(jsonPath("$.token").saveAs("authToken"))
  )
    .pause(1)
    .exec(
      http("Verify Authentication")
        .get("/profile")
        .header("Authorization", "Bearer ${authToken}")
        .check(status.is(200))
    )
}
