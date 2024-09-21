package com.systemvi.collections

import com.systemvi.collections.lists.LinkedList

object Main{
  def main(args: Array[String]): Unit = {
    val list=new LinkedList()
    for(i<-0 until 10){
      list.add(i,i)
    }
    for (i <- 0 until 10) {
      println(list.get(i))
    }
  }
}