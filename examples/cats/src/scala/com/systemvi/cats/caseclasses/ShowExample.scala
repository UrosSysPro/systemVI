package com.systemvi.cats.caseclasses

import cats._
import cats.implicits._
import org.joml.Vector2f



object DisplayExample {
  object Implicits {
    given Show[Int] = (value: Int) => value.toString

    given Show[Vector2f] = (value: Vector2f) => s"x: ${value.x}, y: ${value.y}"
  }
  
  def main(args: Array[String]): Unit = {
    import Implicits.given
    println(Vector2f().show)
  }
}