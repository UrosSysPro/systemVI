import Versions.*
import sbt.*

object ServerDependencies {
  val cats = "org.typelevel" %% "cats-core" % catsVersion
  val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion

  val log4cats  = "org.typelevel" %% "log4cats-slf4j" % log4catsVersion
  val logback = "ch.qos.logback" % "logback-classic" % "1.5.21"

  val http4s: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-ember-server",
    "org.http4s" %% "http4s-ember-client",
    "org.http4s" %% "http4s-circe",
  ).map(_ % http4sVersion)

  val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  val sqlite = "org.xerial" % "sqlite-jdbc" % "3.49.1.0"

  val doobie: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-core"      ,
    "org.tpolecat" %% "doobie-h2"        ,          // H2 driver 1.4.200 + type mappings.
    "org.tpolecat" %% "doobie-hikari"    ,          // HikariCP transactor.
    "org.tpolecat" %% "doobie-specs2"    ,          // Specs2 support for typechecking statements.
  ).map(_ % doobieVersion)

  val serverDependencies: Seq[ModuleID] = Seq(
    cats,
    catsEffect,
    log4cats,
    logback,
    sqlite
  ) ++ http4s ++ circe ++ doobie
}
