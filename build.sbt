import sbt.Keys._
import sbt._
import de.heikoseeberger.sbtheader.HeaderPattern
import Dependencies._

name := "scale"

autoScalaLibrary := false

val copyright = headers := Map(
  "scala" -> (
    HeaderPattern.cStyleBlockComment,
    """ /*
      |  * Copyright (c) 2017 Dany Lee
      |  *
      |  * Licensed under the Apache License, Version 2.0 (the "License");
      |  * you may not use this file except in compliance with the License.
      |  * You may obtain a copy of the License at
      |  *
      |  *    http://www.apache.org/licenses/LICENSE-2.0
      |  *
      |  * Unless required by applicable law or agreed to in writing, software
      |  * distributed under the License is distributed on an "AS IS" BASIS,
      |  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      |  * See the License for the specific language governing permissions and
      |  * limitations under the License.
      |  */
      |
      | """.stripMargin
  )
)


val setts = Seq(
  organization := "com.github.danymarialee",
  version := "1.0.0",
  scalaVersion := Versions.scala,
  crossScalaVersions := Versions.scalas,
  releaseCrossBuild := false,
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  copyright,
  licenses := Seq(
    ("Apache License, Version 2.0", url("https://www.apache.org/licenses/LICENSE-2.0"))
  ),
  homepage := Some(url("https://github.com/DanyMariaLee/scale")),
  sonatypeProfileName := "com.github.danymarialee",
  pgpReadOnly := false,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  publishTo <<= version { (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra in Global := {
    <developers>
      <developer>
        <id>DanyMariaLee</id>
        <name>Marina Sigaeva</name>
        <url>http://twitter.com/besseifunction</url>
      </developer>
    </developers>
  },
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/DanyMariaLee"),
      "scm:git:github.com/DanyMariaLee/scale",
      Some("scm:git:git@github.com:DanyMariaLee/scale.git")
    )
  ),
  credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", "", ""),
  mainClass in assembly := Some("up.scale.Main"),
  assemblyJarName in assembly := "scale.jar"
)

lazy val root =
  Project(id = "scale", base = file("."))
    .settings(setts)
    .settings(libraryDependencies ++= allLibs(scalaVersion.value))
