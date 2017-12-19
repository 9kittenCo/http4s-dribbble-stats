name := "http4s-dribbble-stats"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.4"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  "Bintary JCenter" at "http://jcenter.bintray.com"
)

val Http4sVersion = "0.17.6"
val Specs2Version = "4.0.0"
val LogbackVersion = "1.2.3"
val circeVersion = "0.8.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies ++= http4s
libraryDependencies ++= circe
libraryDependencies ++= fs2

def http4s = Seq(
  "org.http4s" %% "http4s-dsl"          % Http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s" %% "http4s-circe"        % Http4sVersion
)

def fs2 = Seq(
  "co.fs2" %% "fs2-core"                % "0.9.7",
  "co.fs2" %% "fs2-io" % "0.9.7"
)

def circe = Seq(
  "io.circe" %% "circe-core"            % circeVersion,
  "io.circe" %% "circe-generic"         % circeVersion,
  "io.circe" %% "circe-generic-extras"  % circeVersion,
  "io.circe" %% "circe-parser"          % circeVersion
)

val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:_",
  "-unchecked",
  "-Xlint:_",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused-import",
  "-Ypartial-unification",
  "-Ywarn-value-discard"
//  "-Ylog-classpath"
)
scalacOptions in Compile ++= compilerOptions