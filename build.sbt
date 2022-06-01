import com.arpnetworking.sbt.typescript.Import.TypescriptKeys._

name := """ski-resort-dashboard-backend"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).aggregate(ts)
lazy val ts = (project in file("front-end")).enablePlugins(SbtWeb).settings(
    logLevel := Level.Debug,
    Compile / unmanagedSourceDirectories += baseDirectory.value /"typescript" ,
    Assets / sourceDirectory := baseDirectory.value /"typescript",
    Assets / includeFilter := GlobFilter("*.ts") | GlobFilter("*.js") 
)

scalaVersion := "2.13.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += ws
libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.2.1"
libraryDependencies += jdbc
libraryDependencies += "org.postgresql" % "postgresql" % "42.3.1"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
//play.sbt.routes.RoutesKeys.routesImport += "util.Binders._"