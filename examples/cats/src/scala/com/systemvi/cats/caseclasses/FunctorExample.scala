package com.systemvi.cats.caseclasses

import cats.*
import cats.implicits.*

case class Node[F](value: F, left: Node[F]=null, right: Node[F]=null)

object FunctorExample {

  given Show[Node[Int]] = (node: Node[Int]) => {
    def show[F](padding: String, node: Node[F]): String =
      if node != null then
        s"""${node.value}
           |$padding${show(padding++"\t", node.left)}
           |$padding${show(padding++"\t", node.right)}""".stripMargin
      else "null"

    show("", node)
  }

  def main(args: Array[String]): Unit = {
    val tree = Node(1,
      Node(2,
        Node(10,
          Node(10),
          Node(10),
        )
      ),
      Node(3,
        Node(3),
        Node(3),
      ),
    )
    println(tree.show)
  }
}
