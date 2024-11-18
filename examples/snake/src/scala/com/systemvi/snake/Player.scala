package com.systemvi.snake

import com.systemvi.engine.texture.TextureRegion
import com.systemvi.engine.ui.utils.data.Colors
import org.joml.{Vector2i, Vector4f}

case class SnakePart(position:Vector2i)
object SnakePart {
  val size:Int=20
}

class Player {
  val color:Vector4f=Colors.green400
  var parts:Array[SnakePart]=(for(i<-0 until 10)yield SnakePart(position = Vector2i(10,10))).toArray
  val direction:Vector2i=Vector2i()

  def move(): Unit = {
    var i=parts.length-1
    while(i>0){
      parts(i).position.set(parts(i-1).position)
      i-=1
    }
    parts.head.position.add(direction)
  }
  def add(): Unit = {
    parts:+=SnakePart(Vector2i(parts.last.position))
  }

  private def getPadding(i:Int): Int = {
    Math.max(20-i*3,0)
  }

  def draw(renderer:ShapeRenderer,region:TextureRegion,a:Float): Unit = {
    parts.tail.zipWithIndex.foreach((part,index)=>{
      val i=index
      val len=parts.tail.length
      val p1=getPadding(len-i).toFloat
      val p2=getPadding(len-i-1).toFloat
      val padding=p2*a+(1-a)*p1
      renderer.draw(Square(
        part.position.x.toFloat*SnakePart.size+padding/2,
        part.position.y.toFloat*SnakePart.size+padding/2,
        SnakePart.size.toFloat-padding,
        color,
        region
      ))
    })
    val first=parts.head.position
    val second=parts(1).position
    val x=first.x*a+(1-a)*second.x
    val y=first.y*a+(1-a)*second.y
    renderer.draw(Square(
      x*SnakePart.size,
      y*SnakePart.size,
      SnakePart.size.toFloat,
      color,
      region
    ))
  }
}
