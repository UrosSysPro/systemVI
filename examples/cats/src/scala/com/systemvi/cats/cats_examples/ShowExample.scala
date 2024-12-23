package com.systemvi.cats.cats_examples

import cats.*
import cats.implicits.*
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