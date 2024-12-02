package com.systemvi.scala

import scala.annotation.targetName

private case class Point(x: Int, y: Int) {
  @targetName("add")
  def +(p: Point): Point = Point(x + p.x, y + p.y)

  
  def `plus`(p: Point): Point = Point(x + p.x, y + p.y)


  @targetName("increment")
  def ++(): Point = Point(x + 1, y + 1)

  @targetName("arrow")
  def ==>(a: Int): Unit = println("strelica operator")
}

object OperatorOverloading {
  def main(args: Array[String]): Unit = {
    val a = Point(2, 2)
    val b = Point(2, 2)
    //svaka metoda koja prima jedan parametar je binarni operator
    var point = a + b
    point = a `plus` b
    //metoda moze da ima skoro svaki ascii karakter kao ime
    point = point.++()
    //mozemo da izmislimo svoje operatore
    point ==> 2
  }
}
