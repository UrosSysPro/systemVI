package com.systemvi.breakout

object Main{
  def main(args: Array[String]): Unit = {
    println("hello")
    new BreakOut(3,3,60).run()
  }
}