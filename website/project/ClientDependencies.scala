import Versions.*
import sbt.Keys.libraryDependencies
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.*

object ClientDependencies {

  val clientDependencies = libraryDependencies := Seq(
    "org.scala-js" %%% "scalajs-dom" % "2.8.0",
    "com.raquo" %%% "laminar" % "17.0.0",
    "com.raquo" %%% "waypoint" % "10.0.0-M1",
    "org.typelevel" %%% "cats-core" % catsVersion,
  )++ Seq(
    "io.circe" %%% "circe-core",
    "io.circe" %%% "circe-generic",
    "io.circe" %%% "circe-parser"
  ).map(_ % circeVersion)
}
