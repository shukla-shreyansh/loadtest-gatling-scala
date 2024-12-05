package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.check.HttpCheck
import scala.concurrent.duration._
import java.util.UUID

abstract class BaseSimulation extends Simulation {

  protected val environment: String = sys.env.getOrElse("TEST_ENV", "local")

  protected def getEnvConfig(key: String, defaultValue: String = ""): String = {
    val configValue = sys.env.getOrElse(key, defaultValue)
    if (configValue == defaultValue) {
      println(s"[Warning] Missing environment variable: $key. Using default value: $defaultValue")
    }
    configValue
  }

  // Shared HTTP protocol configuration
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(getEnvConfig("BASE_URL", "https://qa.com"))
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader(s"Gatling Performance Test [${UUID.randomUUID()}]")
    .connectionHeader("keep-alive")
    .disableFollowRedirect
    .disableAutoReferer

  // Default headers for all requests
  val defaultHeaders: Map[String, String] = Map(
    "Accept-Language" -> "en-US,en;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Cache-Control"   -> "no-cache"
  )

  // Generalized authentication request
  def authenticatedRequest(endpoint: String, method: String = "GET", tokenKey: String = "authToken"): ChainBuilder = {
    exec(session => {
      val token = sys.env.getOrElse(tokenKey, session(tokenKey).asOption[String].getOrElse("default-token"))
      session.set(tokenKey, token)
    }).exec(
      method.toUpperCase match {
        case "GET" =>
          http(s"Authenticated GET Request to $endpoint")
            .get(endpoint)
            .header("Authorization", session => s"Bearer ${session(tokenKey).as[String]}")
            .check(status.not(401), status.not(403))

        case "POST" =>
          http(s"Authenticated POST Request to $endpoint")
            .post(endpoint)
            .header("Authorization", session => s"Bearer ${session(tokenKey).as[String]}")
            .check(status.not(401), status.not(403))

        case "PUT" =>
          http(s"Authenticated PUT Request to $endpoint")
            .put(endpoint)
            .header("Authorization", session => s"Bearer ${session(tokenKey).as[String]}")
            .check(status.not(401), status.not(403))

        case _ =>
          http(s"Authenticated Request to $endpoint")
            .get(endpoint) // Default to GET
            .header("Authorization", session => s"Bearer ${session(tokenKey).as[String]}")
            .check(status.not(401), status.not(403))
      }
    )
  }

  // Response checks to ensure no errors occur during the test
  def errorChecks: List[HttpCheck] = List(
    status.not(404),
    status.not(500),
    status.not(503),
    responseTimeInMillis.lte(5000)
  )

  // Performance threshold checks
  def performanceThresholdCheck(maxResponseTime: Int = 2000, minSuccessRate: Double = 0.99): List[HttpCheck] = {
    List(
      responseTimeInMillis.lte(maxResponseTime)
    )
  }

  // Logging performance metrics
  def logPerformanceMetrics(scenarioName: String): ChainBuilder = {
    exec { session =>
      println(s"[Performance Log] Scenario: $scenarioName")
      println(s"Session Timestamp: ${java.time.LocalDateTime.now()}")
      println(s"Session Data Keys: ${session.attributes.keys}")
      session
    }
  }

  def variablePause(min: Int = 1, max: Int = 5): ChainBuilder = {
    pause(min.seconds, max.seconds)
  }

  def injectCorrelationId: ChainBuilder = {
    exec(session => {
      val correlationId = UUID.randomUUID().toString
      session.set("correlationId", correlationId)
    })
  }
}
