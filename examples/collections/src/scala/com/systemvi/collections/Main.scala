package com.systemvi.collections

import com.systemvi.collections.lists.LinkedList

import scala.jdk.CollectionConverters._

object Main{
  def main(args: Array[String]): Unit = {
    val list=new LinkedList[Int]()
    for(i<-0 until 10){
      list.addLast(i)
    }
    for(item<-list.asScala){
      println(item)
    }
  }
}