package com.systemvi.engine.math

class Bezier1f (val points:Array[Float]){
  val koefecijenti:Array[Float]=Array.ofDim(points.length)

  for(i<- 0 until points.length){
    koefecijenti(i) = Functions.kombinacija(points.length-1,i)
  }

  def get(t:Float): Float = {
    var value:Float=0
    val n = points.length-1
    for(i<-0 to n){
      value += koefecijenti(i)*points(i)*Functions.stepen(1-t,n-i)*Functions.stepen(t,i)
    }

    value
  }
}
