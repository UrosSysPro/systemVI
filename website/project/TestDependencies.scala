import Versions.*
import sbt.*
import sbt.Keys.libraryDependencies

object TestDependencies{

  val testDependencies = libraryDependencies := Seq(
    "org.typelevel" %% "cats-core"      % catsVersion,
    "org.typelevel" %% "cats-effect"    % catsEffectVersion,
    "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
    "ch.qos.logback" % "logback-classic" % logBackVersion,
    "org.xerial" % "sqlite-jdbc" % sqliteVersion,
  ) ++ Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion) ++ Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-h2",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-specs2",
  ).map(_ % doobieVersion)
}
