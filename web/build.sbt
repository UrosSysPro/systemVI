import ServerDependencies.*
import ClientDependencies.*
import CommonDependencies.*
import TestDependencies.*
import com.typesafe.sbt.packager.docker.DockerPermissionStrategy
import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / parallelExecution := true
ThisBuild / organization  := "net.systemvi"
ThisBuild / version       := "0.7"
ThisBuild / scalaVersion  := "3.3.3"

lazy val web = project.in(file(".")).aggregate(websiteClient,websiteServer)

lazy val websiteCommon = crossProject(JSPlatform,JVMPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
  .in(file("website/common"))
  .settings(
    commonDependencies
  )

lazy val websiteClient = project.in(file("website/client"))
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
  .dependsOn(websiteCommon.js)

lazy val websiteServer = project.in(file("website/server"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    Compile / run / fork := true,
    Compile / run / connectInput := true,
    dockerBaseImage := "eclipse-temurin:25",
    Docker / daemonUser := "ubuntu",
    Docker / daemonUserUid := Some("1000"),
    Docker / daemonGroupGid := Some("1000"),
    libraryDependencies ++= serverDependencies,
  )
  .dependsOn(websiteCommon.jvm)

lazy val mainMenuServer = project.in(file("main_menu/server"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    Compile / run / fork := true,
    Compile / run / connectInput := true,
    dockerBaseImage := "eclipse-temurin:25",
    Docker / daemonUser := "ubuntu",
    Docker / daemonUserUid := Some("1000"),
    Docker / daemonGroupGid := Some("1000"),
    libraryDependencies ++= serverDependencies,
  )
