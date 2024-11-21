package com.systemvi.cats.caseclasses

import cats.*
import cats.implicits.*
import org.joml.Vector2f

case class Node[F](value:F,left:Node[F],right:Node[F])

object FunctorExample {
  
  given Show[Node[Int]] = (node:Node[Int]) => {
    ""
  }
    
  import SemigroupExample.Implicits.given_Show_Vector2f

  def main(args: Array[String]): Unit = {
    val tree=Node[Int](
      1,
      Node[Int](
        2,null,null
      ),
      Node[Int](
        3,null,null
      ),
    )
  }
}
