import ServerDependencies.*
import ClientDependencies.*
import CommonDependencies.*
import TestDependencies.*

import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / organization := "net.systemvi"
ThisBuild / version      := "0.6"
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
    testDependencies
  )
  .dependsOn(common.jvm)
