package feeders

import scala.io.Source
import scala.util.Random
import io.gatling.core.feeder.Feeder

object UserDataFeeder {
  def generateUsers(count: Int): Feeder[String] = {
    (1 to count).map { i =>
      Map(
        "username" -> s"user_$i",
        "password" -> s"password_$i",
        "matchId" -> s"match_${Random.nextInt(1000)}"
      )
    }.toIterator
  }

  // Optional method to load users from external source
  def loadUsersFromFile(filePath: String): Feeder[String] = {
    Source.fromFile(filePath)
      .getLines()
      .map { line =>
        val Array(username, password) = line.split(",")
        Map(
          "username" -> username,
          "password" -> password,
          "matchId" -> s"match_${Random.nextInt(1000)}"
        )
      }
      .toIterator
  }
}