import scala.util.Try

name := "reprobate"

organization := "im.mange"

version := Try(sys.env("TRAVIS_BUILD_NUMBER")).map("0.0." + _).getOrElse("1.0-SNAPSHOT")

scalaVersion := "2.11.5"

libraryDependencies ++= {
  Seq(
    "io.shaka" %% "naive-http" % "48",
    "ch.qos.logback" % "logback-classic" % "1.0.6",
    "im.mange" %% "jetboot" % "0.0.40",
    "im.mange" %% "little-server" % "0.0.8",
    "im.mange" %% "shoreditch-api" % "0.0.60",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  )
}