package com.systemvi.engine.math

object Functions {
  def sum(start:Int,end:Int,f:Int=>Float):Float={
    var result:Float=0
    for(i<-start to end)result+=f(i)
    result
  }
  def product(start:Int,end:Int,f:Int=>Float):Float={
    var result:Float=0
    for(i<-start to end)result*=f(i)
    result
  }
  def integral(start:Float,end:Float,f:Float=>Float):Float={
    var result:Float=0
    var dx:Float=0.0001f
    var x=start
    while (x<end){
      result+=f(x)*dx
      x+=dx
    }
    result
  }
}
