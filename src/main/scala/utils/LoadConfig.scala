package utils

import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object LoadConfig {
  private val config: Config = ConfigFactory.load()

  // Load Testing Configurations
  object TestScenario {
    def getUserInjectionStrategy: String =
      config.getString("load-test.scenario.injection-strategy")

    def getMaxConcurrentUsers: Int = 
      config.getInt("load-test.scenario.max-concurrent-users")

    def getRampUpDuration: Duration = 
      config.getInt("load-test.scenario.ramp-up-duration").seconds

    def getTestDuration: Duration = 
      config.getInt("load-test.scenario.test-duration").minutes
  }

  // Performance Thresholds
  object PerformanceThreshold {
    def getMaxResponseTime: Int = 
      config.getInt("load-test.performance.max-response-time-ms")

    def getSuccessRateThreshold: Double = 
      config.getDouble("load-test.performance.success-rate-threshold")

    def getErrorThresholdPercent: Double = 
      config.getDouble("load-test.performance.error-threshold-percent")
  }

  // Environment Configuration
  object Environment {
    def getBaseUrl: String = 
      config.getString("environment.base-url")

    def getApiVersion: String = 
      config.getString("environment.api-version")

    def isSecureConnection: Boolean = 
      config.getBoolean("environment.secure-connection")
  }

  // Logging and Monitoring
  object Monitoring {
    def isMetricsEnabled: Boolean = 
      config.getBoolean("monitoring.metrics-enabled")

    def getPrometheusEndpoint: Option[String] = 
      if (config.hasPath("monitoring.prometheus-endpoint")) 
        Some(config.getString("monitoring.prometheus-endpoint"))
      else None
  }
}