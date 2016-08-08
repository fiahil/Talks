name := "consumer"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka_2.11" % "0.10.0.0",
  "com.sksamuel.elastic4s" %% "elastic4s-core" % "2.3.0"
)
