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

lazy val web = project.in(file(".")).aggregate(websiteClient,websiteServer,mainMenuServer)

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

lazy val mainMenuCommon = crossProject(JSPlatform,JVMPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
  .in(file("main_menu/common"))
  .settings(
    commonDependencies
  )

lazy val mainMenuClient = project.in(file("main_menu/client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("main_menu")))
    },
    clientDependencies
  )
  .dependsOn(mainMenuCommon.js)

lazy val mainMenuServer = project.in(file("main_menu/server"))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    Compile / run / fork := true,
    Compile / run / connectInput := true,
    dockerBaseImage := "eclipse-temurin:25",
    Docker / daemonUser := "ubuntu",
    Docker / daemonUserUid := Some("1000"),
    Docker / daemonGroupGid := Some("1000"),
    // Image published to the Gitea container registry as
    // `gitea.karaleic.com/uros/main-menu-server`.
    Docker / dockerRepository := Some("gitea.karaleic.com/uros"),
    Docker / packageName := "main-menu-server",
    libraryDependencies ++= serverDependencies,
    libraryDependencies ++= serverTestDependencies,
  )
  .dependsOn(mainMenuCommon.jvm)
