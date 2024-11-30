package com.systemvi.cats.caseclasses

import cats.*
import cats.implicits.*
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object SemigroupalExample {
  
  
  def main(args: Array[String]): Unit = {
    val l1=List(1,2,3,4,5)
    val l2=List(1,2,3,4,5)
    val product=Semigroupal[List].product[Int,Int](l1,l2)
    println(product)

    val o1=2.some
    val o2=3.some
    val p=o1.product(o2)
    println(p)
  }
}

