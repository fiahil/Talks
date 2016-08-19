name := "consumer"

organization := "pogo"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "org.apache.kafka" % "kafka_2.11" % "0.10.0.0",
    "com.sksamuel.elastic4s" %% "elastic4s-core" % "2.3.0",
    "com.typesafe.play" % "play-json_2.11" % "2.5.4",
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "com.typesafe.akka" %% "akka-actor" % akkaV
  )
}
