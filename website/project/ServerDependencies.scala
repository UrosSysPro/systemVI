import Versions.*
import sbt.*

object ServerDependencies {
  val cats = "org.typelevel" %% "cats-core" % catsVersion
  val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion

  val log4cats  = "org.typelevel" %% "log4cats-slf4j" % log4catsVersion
  val logback = "ch.qos.logback" % "logback-classic" % "1.5.21"

  val serverDependencies: Seq[ModuleID] = Seq(
    cats,
    catsEffect,

  )
}
