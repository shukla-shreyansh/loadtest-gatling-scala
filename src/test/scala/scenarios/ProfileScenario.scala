package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class ProfileScenario {

  val profileScenario = exec(
    http("Load User Profile")
      .get("/profile")
      .check(status.is(200))
      .check(jsonPath("$.username").exists)
  )
    .pause(2)
    .exec(
      http("Update Profile Information")
        .put("/profile")
        .body(StringBody("""{"bio": "Updated test bio", "location": "Test City"}"""))
        .check(status.is(200))
    )
    .pause(1)
    .exec(
      http("Change Profile Picture")
        .post("/profile/picture")
        .bodyPart(RawFileBodyPart("picture", "test-picture.jpg"))
        .check(status.is(200))
    )
}
