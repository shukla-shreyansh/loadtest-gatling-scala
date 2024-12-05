package utils

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

object PerformanceMetrics {
  def calculateResponseTimePercentiles(scenario: ScenarioBuilder): ScenarioBuilder = {
    scenario
      .pace(5)
      .exec(
        session => {
          // Custom session attributes for tracking
          session.set("requestStartTime", System.currentTimeMillis())
        }
      )
  }

  // Detailed Metrics Collector
  class DetailedMetricsCollector {
    private var requestTimings: List[Long] = List()
    private var errorCount: Int = 0
    private var totalRequests: Int = 0

    def recordRequestTiming(timing: Long): Unit = {
      requestTimings = timing :: requestTimings
    }

    def recordError(): Unit = {
      errorCount += 1
    }

    def recordTotalRequest(): Unit = {
      totalRequests += 1
    }

    def getMetricsSummary: Map[String, Any] = {
      Map(
        "averageResponseTime" -> calculateAverageResponseTime(),
        "p95ResponseTime" -> calculatePercentile(95),
        "errorRate" -> calculateErrorRate(),
        "totalRequests" -> totalRequests
      )
    }

    private def calculateAverageResponseTime(): Double = {
      if (requestTimings.nonEmpty) 
        requestTimings.sum.toDouble / requestTimings.size 
      else 0.0
    }

    private def calculatePercentile(percentile: Int): Long = {
      val sortedTimings = requestTimings.sorted
      val index = (sortedTimings.size * percentile / 100.0).toInt
      sortedTimings(index)
    }

    private def calculateErrorRate(): Double = {
      if (totalRequests > 0) 
        (errorCount.toDouble / totalRequests) * 100 
      else 0.0
    }
  }

  // Performance Report Generator
  def generatePerformanceReport(metrics: DetailedMetricsCollector): String = {
    val summary = metrics.getMetricsSummary
    s"""
      |Performance Test Report
      |----------------------
      |Total Requests: ${summary("totalRequests")}
      |Average Response Time: ${summary("averageResponseTime")} ms
      |95th Percentile Response Time: ${summary("p95ResponseTime")} ms
      |Error Rate: ${summary("errorRate")}%
    """.stripMargin
  }
}