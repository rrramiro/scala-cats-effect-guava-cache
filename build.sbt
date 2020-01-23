
name := "scala-cats-effect-guava-cache"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "2.0.0",
  "org.slf4j" % "slf4j-api" % "1.7.30",
  "com.google.guava" % "guava"  % "28.2-jre",
  "io.monix" %% "monix" % "3.1.0" % Test,
  "dev.zio" %% "zio-interop-cats" % "2.0.0.0-RC10" % Test,
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "org.scalamock" %% "scalamock" % "4.4.0" % Test
)

scalafmtOnCompile := true

coverageEnabled := true

smlBuildSettings

//scalacOptions ++=Seq("-print")
