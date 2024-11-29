package com.systemvi.cats.caseclasses

import cats.*
import cats.implicits.*

object MonadExample {
  def main(args: Array[String]): Unit = {
    val a = Monad[Id].pure(2)
    val b: Id[Int] = 3 //.pure[Id]
    val c = for {
      y <- b
      x <- a
    } yield x + y
    val some: Option[Int] = Some(2)
    val none: Option[Int] = None

    val result = some |+| none

    println(s"some |+| none = ${result.show}")
    println(s"a flat = ${result.show}")

    val list: List[Int] = List(1, 2, 3, 4)

    val mapped = list.map[List[Int]](_ => List(1, 2, 3))
    val flatMapped = list.flatMap[Int](_ => List(1, 2, 3))

    println(mapped.show)
    println(flatMapped.show)

    val composition: Option[Int] = Some(2)
      .map(value => value * 2)
      .flatMap(value=>Some(value))
      .map(value =>
        println(s"print inside map $value")
        value
      )

    for{
      x<-composition
      y<-composition
    } {
      println(s"print inside for ${x+y}")
    }
  }
}
