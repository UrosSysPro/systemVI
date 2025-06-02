import org.scalajs.linker.interface.ModuleSplitStyle

val circeVersion = "0.14.13"

lazy val website = project.in(file("client"))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .settings(
    scalaVersion := "3.3.3",

    scalaJSUseMainModuleInitializer := true, // Tell Scala.js that this is an application with a main method

    /* Configure Scala.js to emit modules in the optimal way to
     * connect to Vite's incremental reload.
     * - emit ECMAScript modules
     * - emit as many small modules as possible for classes in the "website" package
     * - emit as few (large) modules as possible for all other classes
     *   (in particular, for the standard library)
     */
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("website")))
    },

    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0", //for dom types
    libraryDependencies += "com.raquo" %%% "laminar" % "17.0.0",  // Requires Scala.js 1.13.2+
    libraryDependencies += "org.typelevel" %%% "cats-core" % "2.13.0",
    libraryDependencies += "com.raquo" %%% "waypoint" % "10.0.0-M1",   // Depends on Laminar 17.2.0 & URL DSL 0.6.2
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core",
      "io.circe" %%% "circe-generic",
      "io.circe" %%% "circe-parser"
    ).map(_ % circeVersion), //json parser
  )

lazy val server=project.in(file("server"))
  .settings(
    scalaVersion:="3.3.3",
  ).settings(
//    Compile / mainClass := Some("net.systemvi.server.Main"),
//     / mainClass := Some("net.systemvi.server.Main"),
//    Compile / jar := "app.jar",
  )