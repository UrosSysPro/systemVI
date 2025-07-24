import org.scalajs.linker.interface.ModuleSplitStyle

val circeVersion = "0.14.13"
//val http4sVersion ="1.0.0-M44"
val http4sVersion = "0.23.30"

ThisBuild / organization := "net.systemvi"
ThisBuild / version      := "0.5"
ThisBuild / scalaVersion := "3.3.3"

lazy val root = project.in(file(".")).aggregate(website,server)

lazy val common = crossProject(JSPlatform,JVMPlatform)
  .crossType(CrossType.Pure)
  .withoutSuffixFor(JVMPlatform)
  .in(file("common"))
  .settings(
    libraryDependencies += "org.typelevel" %%% "cats-core" % "2.13.0",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core",
      "io.circe" %%% "circe-generic",
      "io.circe" %%% "circe-parser"
    ).map(_ % circeVersion),
  )

lazy val website = project.in(file("client"))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .settings(
    scalaJSUseMainModuleInitializer := true, // Tell Scala.js that this is an application with a main method
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("website")))
    },
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",  // scala.js for dom types
    libraryDependencies += "com.raquo" %%% "laminar" % "17.0.0",        // laminar library
    libraryDependencies += "com.raquo" %%% "waypoint" % "10.0.0-M1",    // waypoint for laminar library
    libraryDependencies += "org.typelevel" %%% "cats-core" % "2.13.0",  // scala cats
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core",
      "io.circe" %%% "circe-generic",
      "io.circe" %%% "circe-parser"
    ).map(_ % circeVersion), //json parser
  )
  .dependsOn(common.js)

lazy val server=project.in(file("server"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    Compile / run / fork := true,
    Compile / run / connectInput := true,
    libraryDependencies += "org.typelevel" %%% "cats-core"      % "2.13.0", //cats dependency
    libraryDependencies += "org.typelevel" %%% "cats-effect"    % "3.6.1", // cats effect dependency
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.17",
    libraryDependencies += "org.typelevel" %% "log4cats-slf4j" % "2.7.1",  // Direct Slf4j Support - Recommended
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl",
      "org.http4s" %% "http4s-ember-server",
      "org.http4s" %% "http4s-ember-client",
      "org.http4s" %% "http4s-circe",
    ).map(_ % http4sVersion),
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core",
      "io.circe" %%% "circe-generic",
      "io.circe" %%% "circe-parser"
    ).map(_ % circeVersion),
    libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.49.1.0", //sqlite
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core"      % "1.0.0-RC8",
//      "org.tpolecat" %% "doobie-h2"        % "1.0.0-RC8",          // H2 driver 1.4.200 + type mappings.
//      "org.tpolecat" %% "doobie-hikari"    % "1.0.0-RC8",          // HikariCP transactor.
//      "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC8",          // Postgres driver 42.7.5 + type mappings.
//      "org.tpolecat" %% "doobie-specs2"    % "1.0.0-RC8" % "test", // Specs2 support for typechecking statements.
//      "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC8" % "test"  // ScalaTest support for typechecking statements.
    )
  )
  .dependsOn(common.jvm)