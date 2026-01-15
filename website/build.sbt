import Versions.*
import ServerDependencies.*
import ClientDependencies.*
import CommonDependencies.*
import TestDependencies.*

import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / organization := "net.systemvi"
ThisBuild / version      := "0.5"
ThisBuild / scalaVersion := "3.3.3"

lazy val root = project.in(file(".")).aggregate(client,server)

lazy val common = crossProject(JSPlatform,JVMPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
  .in(file("common"))
  .settings(
    commonDependencies
  )

lazy val client = project.in(file("client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("website")))
    },
    clientDependencies
  )
  .dependsOn(common.js)

lazy val server = project.in(file("server"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    Compile / run / fork := true,
    Compile / run / connectInput := true,
    serverDependencies
  )
  .dependsOn(common.jvm)

//tests

lazy val testSqlite = project.in(file("tests/sqlite"))
  .settings(
    Compile / run / fork := true,
    Compile / run / connectInput := true,
//    cats
    libraryDependencies += "org.typelevel" %% "cats-core"      % "2.13.0", //cats dependency
    libraryDependencies += "org.typelevel" %% "cats-effect"    % "3.6.3", // cats effect dependency
//    logger
    libraryDependencies += "org.typelevel" %% "log4cats-slf4j" % "2.7.1",  // Direct Slf4j Support - Recommended
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.21",
//    json
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion),
//    doobie
    libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.49.1.0", //sqlite
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core"      ,
      "org.tpolecat" %% "doobie-h2"        ,          // H2 driver 1.4.200 + type mappings.
      "org.tpolecat" %% "doobie-hikari"    ,          // HikariCP transactor.
      "org.tpolecat" %% "doobie-specs2"    ,          // Specs2 support for typechecking statements.
    ).map(_ % doobieVersion),
  )
  .dependsOn(common.jvm)
