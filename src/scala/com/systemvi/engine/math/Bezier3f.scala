package com.systemvi.engine.math

import org.joml.Vector3f

class Bezier3f (val points:Array[Vector3f]){
  val koefecijenti:Array[Float]=Array.ofDim(points.length)

  for(i<- 0 until points.length){
    koefecijenti(i) = Functions.kombinacija(points.length-1,i)
  }

  def get(t:Float): Vector3f = {
    var value:Vector3f=new Vector3f(0,0,0)
    val n = points.length-1
    for(i<-0 to n){
      //value += koefecijenti(i)*points(i)*Functions.stepen(1-t,n-i)*Functions.stepen(t,i)
    }

    value
  }
}
