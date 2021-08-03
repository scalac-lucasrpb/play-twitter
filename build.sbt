
name := "demo"
organization := "demo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//scalaVersion := "2.13.6"

libraryDependencies += guice
libraryDependencies += filters
libraryDependencies += logback
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "com.datastax.oss" % "java-driver-core-shaded" % "4.9.0"
libraryDependencies += "com.github.3tty0n" % "jwt-scala_2.12" % "1.3.0"

//libraryDependencies += "com.eed3si9n" %% "sbt-assembly" % "1.0.0"

dependencyOverrides += "com.google.inject" % "guice" % "5.0.1"
dependencyOverrides += "com.typesafe.play" %% "twirl-api" % "1.5.1"

PlayKeys.playDefaultPort := 8000
PlayKeys.playDefaultAddress := "0.0.0.0"

mainClass in assembly := Some("play.core.server.ProdServerStart")
fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

enablePlugins(PlayScala, LauncherJarPlugin)

/*val java17Options = Seq(
  "-XX:+IgnoreUnrecognizedVMOptions",
  "--add-opens=java.base/sun.net.www.protocol.file=ALL-UNNAMED",
  "--add-exports=java.base/sun.net.www.protocol.file=ALL-UNNAMED"
)

fork / run := true
javaOptions / run := java17Options*/

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "demo.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "demo.binders._"
