package com.systemvi.cats.caseclasses

import cats.*
import cats.implicits.*
import org.joml.Vector2f



object SemigroupExample {
  object Implicits {
    given Semigroup[Vector2f] = (x: Vector2f, y: Vector2f) => Vector2f(x).add(y)

    given Show[Vector2f] = (v: Vector2f) => s"x: ${v.x} y: ${v.y}"
  }

  def main(args: Array[String]): Unit = {
    import Implicits.given
    val a = Vector2f(1, 2)
    val b = Vector2f(2, 1)
    val c = a |+| b
    println(c.show)
  }
}
