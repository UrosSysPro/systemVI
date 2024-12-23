package com.systemvi.cats.cats_examples

import cats.*
import cats.implicits.*

case class Node[F](value: F, left: Node[F]=null, right: Node[F]=null)

object FunctorExample {

  given Show[Node[?]] = (node: Node[?]) => {
    def show[F](padding: String, node: Node[F]): String =
      if node != null then
        s"""${node.value}
           |$padding${show(padding++"\t", node.left)}
           |$padding${show(padding++"\t", node.right)}""".stripMargin
      else "null"

    show("", node)
  }

//  given Show[Node[String]] = (node: Node[String]) => {
//    def show[F](padding: String, node: Node[F]): String =
//      if node != null then
//        s"""${node.value}
//           |$padding${show(padding ++ "\t", node.left)}
//           |$padding${show(padding ++ "\t", node.right)}""".stripMargin
//      else "null"
//
//    show("", node)
//  }

  given Functor[Node] = new Functor[Node]:
    override def map[A, B](fa: Node[A])(f: A => B): Node[B] = {
      if(fa!=null){
        Node[B](
          f(fa.value),
          map(fa.left)(f),
          map(fa.right)(f)
        )
      }else{
        null
      }
    }

  def main(args: Array[String]): Unit = {
    val tree:Node[?] = Node(1,
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
    ).map(a=>s"value: $a")
    println(tree.show)
  }
}
