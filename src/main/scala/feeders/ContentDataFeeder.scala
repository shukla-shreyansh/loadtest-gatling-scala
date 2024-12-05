package feeders

import io.gatling.core.Predef._
import io.gatling.core.feeder.Feeder
import io.gatling.http.Predef._

import scala.util.Random

object ContentDataFeeder {
  object ContentType extends Enumeration {
    type ContentType = Value
    val LIVE_MATCH, HIGHLIGHTS, ORIGINAL_SERIES, NEWS, MOVIE = Value
  }

  object SportCategory extends Enumeration {
    type SportCategory = Value
    val CRICKET, FOOTBALL, KABADDI, TENNIS, HOCKEY = Value
  }

  def generateContentData(count: Int): Feeder[String] = {
    (1 to count).map { i =>
      Map(
        "contentId" -> s"content_$i",
        "contentType" -> randomEnum(ContentType).toString,
        "contentTitle" -> generateContentTitle(),
        "sportCategory" -> randomEnum(SportCategory).toString,
        "streamQuality" -> randomStreamQuality(),
        "languageOptions" -> generateLanguageOptions(),
        "expectedConcurrentViewers" -> Random.nextInt(100000).toString,
        "availableBitrateOptions" -> generateBitrateOptions(),
        "isPremium" -> Random.nextBoolean().toString,
        "accessLevel" -> randomAccessLevel()
      )
    }.toIterator
  }

  // Helper Methods for Data Generation
  private def generateContentTitle(): String = {
    val prefixes = List("Live", "Highlights", "Exclusive", "Classic")
    val sports = List("Cricket", "Football", "Kabaddi", "Tennis")
    val events = List("Match", "Tournament", "Championship", "Series")
    
    s"${prefixes(Random.nextInt(prefixes.size))} ${sports(Random.nextInt(sports.size))} ${events(Random.nextInt(events.size))}"
  }

  private def randomEnum[T <: Enumeration](enum: T): T#Value = {
    val values = enum.values.toList
    values(Random.nextInt(values.size))
  }

  private def randomStreamQuality(): String = {
    val qualities = List("240p", "360p", "480p", "720p", "1080p", "4K")
    qualities(Random.nextInt(qualities.size))
  }

  private def generateLanguageOptions(): String = {
    val languages = List("English", "Hindi", "Tamil", "Telugu", "Malayalam")
    val selectedLanguages = Random.shuffle(languages).take(Random.nextInt(3) + 1)
    selectedLanguages.mkString(",")
  }

  private def generateBitrateOptions(): String = {
    val bitrates = List("500kbps", "1Mbps", "2Mbps", "5Mbps", "10Mbps")
    val selectedBitrates = Random.shuffle(bitrates).take(Random.nextInt(3) + 1)
    selectedBitrates.mkString(",")
  }

  private def randomAccessLevel(): String = {
    val accessLevels = List("FREE", "PREMIUM", "SUBSCRIBER", "PAY_PER_VIEW")
    accessLevels(Random.nextInt(accessLevels.size))
  }

  // CSV Export Utility
  def exportToCSV(count: Int, filename: String): Unit = {
    import java.io.{File, PrintWriter}
    
    val data = generateContentData(count)
    val file = new File(filename)
    val writer = new PrintWriter(file)
    
    // Headers
    writer.println(
      "contentId,contentType,contentTitle,sportCategory," +
      "streamQuality,languageOptions,expectedConcurrentViewers," +
      "availableBitrateOptions,isPremium,accessLevel"
    )
    
    // Data
    data.foreach { record =>
      writer.println(
        s"${record("contentId")}," +
        s"${record("contentType")}," +
        s"${record("contentTitle")}," +
        s"${record("sportCategory")}," +
        s"${record("streamQuality")}," +
        s"${record("languageOptions")}," +
        s"${record("expectedConcurrentViewers")}," +
        s"${record("availableBitrateOptions")}," +
        s"${record("isPremium")}," +
        s"${record("accessLevel")}"
      )
    }
    
    writer.close()
    println(s"Content data exported to $filename")
  }
}

object ContentDataFeederApp extends App {
  val contentFeeder = ContentDataFeeder.generateContentData(1000)
  
  // Export to CSV
  ContentDataFeeder.exportToCSV(1000, "content.csv")
}