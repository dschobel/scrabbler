import com.github.retronym.SbtOneJar
import sbt._
import Keys._

object ScrabblerBuild extends Build {

  lazy val scalatestLib =  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
  lazy val scalaKryo = "com.romix.scala" % "scala-kryo-serialization" % "0.2-SNAPSHOT"


  lazy val buildSettings = Project.defaultSettings ++  Seq(
    version := "ALPHA", 
    scalaVersion :=  "2.10.1",
    fork in Test := true,
    exportJars := true,
    scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps"),
    libraryDependencies ++= Seq(scalatestLib, scalaKryo)
  )

  lazy val libscrabble = Project(id = "libscrabble", 
                          base = file("libscrabble"),
                          settings = buildSettings ++ SbtOneJar.oneJarSettings)

  lazy val indexer = Project(id = "indexer", 
    base = file("indexer"),
    settings = buildSettings ++ SbtOneJar.oneJarSettings).dependsOn(libscrabble)

  lazy val suggester = Project(id = "suggester", 
    base = file("suggester"),
    settings = buildSettings ++ SbtOneJar.oneJarSettings).dependsOn(libscrabble)

  lazy val qps = Project(id = "qps", 
    base = file("qps"),
    settings = buildSettings ++ SbtOneJar.oneJarSettings).dependsOn(libscrabble)
}
