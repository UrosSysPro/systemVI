import Versions.*
import sbt.*
import sbt.Keys.*
import sbt.Keys.libraryDependencies

object ServerDependencies {
  val cats = "org.typelevel" %% "cats-core" % catsVersion
  val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion

  val log4cats  = "org.typelevel" %% "log4cats-slf4j" % log4catsVersion
  val logback = "ch.qos.logback" % "logback-classic" % logBackVersion

  val http4s = Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-ember-server",
    "org.http4s" %% "http4s-ember-client",
    "org.http4s" %% "http4s-circe",
  ).map(_ % http4sVersion)

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
  ).map(_ % circeVersion)

  val jwtHttp4s =       "dev.profunktor"          %% "http4s-jwt-auth"     % jwtHttp4sVersion
  val jwtScala =        "com.github.jwt-scala"    %% "jwt-core"            % jwtScalaVersion
  val jwtCirce =        "com.github.jwt-scala"    %% "jwt-circe"           % jwtScalaVersion



  val ciris = Seq(
    "is.cir" %% "ciris",
    "is.cir" %% "ciris-circe",
    "is.cir" %% "ciris-circe-yaml",
    "is.cir" %% "ciris-http4s",
  ).map(_ % cirisVersion)

  val sqlite = "org.xerial" % "sqlite-jdbc" % sqliteVersion

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core"      ,
    "org.tpolecat" %% "doobie-h2"        ,          // H2 driver 1.4.200 + type mappings.
    "org.tpolecat" %% "doobie-hikari"    ,          // HikariCP transactor.
    "org.tpolecat" %% "doobie-specs2"    ,          // Specs2 support for typechecking statements.
  ).map(_ % doobieVersion)

  val serverDependencies = Seq(
    cats,
    catsEffect,
    log4cats,
    logback,
    sqlite,
    jwtHttp4s,
    jwtScala,
    jwtCirce,
  ) ++ http4s ++ circe ++ doobie ++ ciris
}
