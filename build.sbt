resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

scalaVersion := "2.13.15"

enablePlugins(GatlingPlugin)

libraryDependencies ++= Seq(
  "io.gatling" % "gatling-core" % "3.13.1",
  "io.gatling" % "gatling-http" % "3.13.1",
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.13.1" % "test",
  "io.gatling" % "gatling-test-framework" % "3.13.1" % "test",
  "com.typesafe" % "config" % "1.4.2"
)

fork in Test := true
javaOptions in Test ++= Seq(
  "--add-opens", "java.base/java.lang=ALL-UNNAMED",
  "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED"
)
