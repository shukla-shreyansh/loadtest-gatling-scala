package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class InteractionScenario {

  val interactionScenario = exec(
    http("Create New Content")
      .post("/content")
      .body(StringBody("""{"contentId": "${contentId}", "title": "Test Content"}"""))
      .check(status.is(201))
  )
    .pause(2)
    .exec(
      http("Interact with Content")
        .post("/content/${contentId}/interact")
        .body(StringBody("""{"type": "like"}"""))
        .check(status.is(200))
    )
}
