package com.systemvi.examples.polygons

import org.joml.Vector2f

class Polygon2(var points:Array[Vector2f]){

}

object Polygon2{
  def apply(points:Array[Vector2f]):Polygon2={
    new Polygon2(points)
  }
}
