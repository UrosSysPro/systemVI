package com.systemvi.cats.caseclasses

import cats.*
import cats.implicits.*

object FoldableExample {
  def main(args: Array[String]): Unit = {
    var folded:Int=0
    val list: List[Int] = List(1, 2, 3, 4, 5)

    folded = Foldable[List].foldLeft[Int, Int](list, 0)(_+_)
    println(s"list = $list")
    println(s"cats folded = $folded")

    folded = list.foldLeft[Int](0)(_+_)
    println(s"list = $list")
    println(s"defaulted folded = $folded")

    val option: Option[List[Int]] = list.some

    val traversed = option.traverse[List, Int](value=>value)
    println(s"traversed = $traversed")

    val sequenced = option.sequence[List, Int]
    println(s"sequenced = $sequenced")
  }
}
