package com.systemvi.engine.math

import org.joml.Vector2f

class Bezier2f (val points:Array[Vector2f]){
  val koefecijenti:Array[Float]=Array.ofDim(points.length)

  for(i<- 0 until points.length){
    koefecijenti(i) = Functions.kombinacija(points.length-1,i)
  }

  def get(t:Float): Vector2f = {
    var value:Vector2f=new Vector2f(0,0)
    val n = points.length-1
    for(i<-0 to n){
      value.add(new Vector2f(points(i)).mul( koefecijenti(i)*Functions.stepen(1-t,n-i)*Functions.stepen(t,i)))
    }

    value
  }
}
