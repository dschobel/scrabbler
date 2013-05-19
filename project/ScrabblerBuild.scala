import sbt._
import Keys._

object ScrabblerBuild extends Build {

  lazy val scalatestLib =  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

  lazy val buildSettings = Project.defaultSettings ++  Seq(
    version := "ALPHA", 
    scalaVersion :=  "2.10.1",
    fork in Test := true,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps"),
    libraryDependencies ++= Seq(scalatestLib)
  )

  lazy val libscrabble = Project(id = "libscrabble", 
                          base = file("libscrabble"),
                          settings = buildSettings)

  lazy val indexer = Project(id = "applications", 
    base = file("applications"),
    settings = buildSettings).dependsOn(libscrabble)
}
