import Versions.*
import sbt.Keys.libraryDependencies
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.*

object CommonDependencies{

  val commonDependencies = libraryDependencies := Seq(
    "org.typelevel" %%% "cats-core" % catsVersion,
  )++ Seq(
    "io.circe" %%% "circe-core",
    "io.circe" %%% "circe-generic",
    "io.circe" %%% "circe-parser"
  ).map(_ % circeVersion)
}
