name := "Lunatech_Assessment"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.3",
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "com.typesafe.akka" %% "akka-http" % "10.0.7",
  "org.scalaz" % "scalaz-core_2.12" % "7.2.14",
  "org.json4s" %% "json4s-native" % "3.5.2",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.7"
)
        