import sbt.Keys._

name := "RedisClient"
version := "3.30.1"

lazy val redisClient = (project in file(".")).settings(coreSettings : _*)

lazy val commonSettings: Seq[Setting[_]] = Seq(
  organization := "tech.ignission",
  scalaVersion := "2.12.10",
  crossScalaVersions := Seq("2.12.11", "2.11.12", "2.10.7", "2.13.2"),

  scalacOptions in Compile ++= Seq( "-unchecked", "-feature", "-language:postfixOps", "-deprecation" ),

  resolvers ++= Seq(
    ("typesafe repo" at "http://repo.typesafe.com/typesafe/releases/").withAllowInsecureProtocol(true)
  )
)

def dockerTestKit(version: String): Seq[ModuleID] = {
  Seq("docker-testkit-scalatest", "docker-testkit-impl-docker-java").map("com.whisk" %% _ % version % Test) :+
    // https://github.com/eclipse-ee4j/jaxb-ri/issues/1222
    "javax.xml.bind" % "jaxb-api" % "2.3.1" % Test
}

lazy val coreSettings = commonSettings ++ Seq(
  name := "RedisClient",
  libraryDependencies ++= Seq(
    "org.apache.commons"      %  "commons-pool2"           % "2.8.0",
    "org.slf4j"               %  "slf4j-api"               % "1.7.29",
    "org.slf4j"               %  "slf4j-log4j12"           % "1.7.29"      % "provided",
    "log4j"                   %  "log4j"                   % "1.2.17"      % "provided",
    "org.scalatest"           %% "scalatest"               % "3.1.0"       % "test"
  ) ++
    (scalaBinaryVersion.value match {
      case "2.10" => dockerTestKit("0.9.8")
      case _ => dockerTestKit("0.9.9")
    })
  ,

  isSnapshot := version.value endsWith "SNAPSHOT",
  ThisBuild / publishTo := sonatypePublishToBundle.value,
  publishMavenStyle := true,
  Test / publishArtifact := false,
  Compile / packageDoc / publishArtifact := true,
  Compile / packageSrc / publishArtifact := true,
  pomIncludeRepository := { _ => false },
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
  homepage := Some(url("https://github.com/ignission")),
  Compile / doc / sources := Seq.empty,
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/ignission/scala-redis"),
      "scm:git:git@github.com/ignission/scala-redis.git"
    )
  ),
  unmanagedResources in Compile += baseDirectory.map( _ / "LICENSE" ).value
)
