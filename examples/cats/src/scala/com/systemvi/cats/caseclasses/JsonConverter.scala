package com.systemvi.cats.caseclasses

import com.systemvi.engine.ui.utils.data.Colors
import org.joml.{Vector2f, Vector4f}

trait Json[F] {
  def toJson(f: F): String
}

case class Point(
                  position: Vector2f,
                  velocity: Vector2f,
                  acceleration: Vector2f,
                  radius: Float,
                  mass: Float,
                  color: Vector4f,
                )

object Json {
  given Json[Int] = (a: Int) => s"$a"

  given Json[Float] = (a: Float) => s"$a"

  given Json[String] = (a: String) => s"\"$a\""

  given Json[Vector4f] = (a: Vector4f) =>
    s"""{
       |  x:${a.x},
       |  y:${a.y},
       |  z:${a.z},
       |  w:${a.w}
       |}""".stripMargin

  given Json[Vector2f] = (a: Vector2f) =>
    s"""{
       |  x:${a.x},
       |  y:${a.y}
       |}""".stripMargin

  given Json[Point] = (a: Point) =>
    s"""{
       |  position:${Json.toJson(a.position)},
       |  velocity:${Json.toJson(a.velocity)},
       |  acceleration:${Json.toJson(a.acceleration)},
       |  radius:${Json.toJson(a.radius)},
       |  mass:${Json.toJson(a.mass)},
       |  color:${Json.toJson(a.color)}
       |}""".stripMargin


  def toJson[F](f: F)(using instance: Json[F]): String = instance.toJson(f)
}

extension (f: Vector4f) {
  def toJson: String = {
    Json.toJson(f)
  }
}
extension (f: Point) {
  def toJson: String = {
    Json.toJson(f)
  }
}

object JsonConverter {
  def main(args: Array[String]): Unit = {
    println(Point(
      position = Vector2f(),
      velocity = Vector2f(),
      acceleration = Vector2f(),
      radius = 2,
      mass = 2,
      color = Colors.green400
    ).toJson)
  }
}