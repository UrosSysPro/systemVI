package com.systemvi.collections

import com.systemvi.collections.lists.LinkedList

object Main{
  def main(args: Array[String]): Unit = {
    val list=new LinkedList[Int]()
    for(i<-0 until 10){
      list.addLast(i)
    }
    for (i <- list) {
      println(list.get(i))
    }
  }
}