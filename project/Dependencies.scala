import sbt._

//noinspection TypeAnnotation
object Dependencies {

  object Versions {
    val scala  = "2.12.2"
    val scalas = Seq("2.11.8", "2.12.2")

    val akkaHttp        = "10.0.6"
    val akka        = "2.5.1"
    val scalatest       = "3.0.3"
    val typesafeConfig  = "1.3.1"
    val json4sVersion = "3.5.2"
    val sprayV = "1.3.3"
  }

  val testLibs = Seq(
    "org.scalatest"           %% "scalatest"            % Versions.scalatest,
    "com.typesafe.akka"       %% "akka-http-spray-json" % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp,
    "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0"
  ).map(_ % "test")

  def mainLibs(scalaVersion: String) = Seq(
    "com.typesafe.akka" %% "akka-slf4j" % Versions.akka,
    "com.typesafe.akka" %% "akka-actor" % Versions.akka,
    "com.typesafe.akka" %% "akka-stream" % Versions.akka,
    "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp,
    "org.slf4j" % "slf4j-simple" % "1.7.23"
  )

  def commonLibs(scalaVersion: String) = mainLibs(scalaVersion) ++ testLibs

}