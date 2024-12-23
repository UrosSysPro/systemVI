package com.systemvi.cats.cats_examples

import cats.*
import cats.implicits.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

object FoldableExample {


  def futurePrint(n:Int) = Future{
    Thread.sleep(n*500)
    println(n)
  }

  def main(args: Array[String]): Unit = {
    var folded:Int=0
    val list: List[Int] = List(1, 2, 3, 4, 5)

    folded = Foldable[List].foldLeft[Int, Int](list, 0)(_+_)
    println(s"list = $list")
    println(s"cats folded = $folded")

    folded = list.foldLeftM[Id,Int](0)(_+_)
    println(s"list = $list")
    println(s"defaulted folded = $folded")

    val option: Option[List[Int]] = list.some

    val traversed = option.traverse[List, Int](value=>value)
    println(s"traversed = $traversed")

    val sequenced = option.sequence[List, Int]
    println(s"sequenced = $sequenced")

    val prints=(0 until 10).map(futurePrint).toList.sequence
    Await.result(prints,100.seconds)
  }
}
