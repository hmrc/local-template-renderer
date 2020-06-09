import PlayCrossCompilation._
import uk.gov.hmrc.DefaultBuildSettings.targetJvm

val appName = "local-template-renderer"

lazy val root = (project in file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(majorVersion := 2)
  .settings(makePublicallyAvailableOnBintray := true)
  .settings(
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.11.12", "2.12.8")
  )
  .settings(
    name := appName,
    targetJvm := "jvm-1.8",
    libraryDependencies ++= PlayCrossCompilation.dependencies(
      shared = Seq(
        "com.typesafe.play" %% "play" % PlayCrossCompilation.version,
        "com.github.spullara.mustache.java" % "compiler" % "0.9.6",
        "com.github.spullara.mustache.java" % "scala-extensions-2.11" % "0.9.6",
        "com.google.guava" % "guava" % "29.0-jre",
        "org.scalactic" %% "scalactic" % "3.0.7",
        "org.scalatest" %% "scalatest" % "3.0.7" % "test",
        "org.pegdown" % "pegdown" % "1.6.0" % "test",
        "com.typesafe.play" %% "play-test" % PlayCrossCompilation.version % "test"),
      play25 = Seq("uk.gov.hmrc" %% "hmrctest" % "3.5.0-play-25" % "test"),
      play26 = Seq("org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test")
    ),
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.typesafeRepo("releases")
    )
  )

playCrossCompilationSettings
