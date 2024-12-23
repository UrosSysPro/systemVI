package com.systemvi.cats.cats_examples

import cats.*
import cats.implicits.*
import org.joml.Vector2f

object MonoidExample {
  given Monoid[Vector2f] = new Monoid[Vector2f]{
    override def combine(x: Vector2f, y: Vector2f): Vector2f = Vector2f(x).add(y)

    override def empty: Vector2f = Vector2f()
  }
  
  import SemigroupExample.Implicits.given_Show_Vector2f

  def main(args: Array[String]): Unit = {
    println((Vector2f(1,2) |+| Vector2f(20)).show)
  }
}
