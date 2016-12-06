name := "scratch"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-core" % "2.3.1",
    "org.elasticsearch.plugin" % "shield" % "2.3.3"
  )
}
