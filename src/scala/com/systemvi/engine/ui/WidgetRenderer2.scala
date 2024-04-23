package com.systemvi.engine.ui

import com.systemvi.engine.model.{Mesh, VertexAttribute}
import com.systemvi.engine.shader.Shader
import org.joml.{Matrix4f, Vector4f}

case class Rect(x:Float,y:Float,width:Float,height:Float,rotation:Float)
case class Border(radius:Float,width:Float,color:Vector4f)
case class Drawable(rect:Rect,color:Vector4f,border:Border,blur:Float,boundry:Rect,glyph:Rect,transform:Matrix4f){
  def writeToArray(array:Array[Float]): Unit = {

  }
}
class WidgetRenderer2 {
  val mesh = new Mesh(
    new VertexAttribute("position",4)
  )
  val size=0.5f
  mesh.setVertexData(Array(
     size,  size ,0,1,
     size, -size ,0,1,
    -size, -size ,0,1,
    -size,  size ,0,1
  ))
  mesh.setIndices(Array(
    0,2,1,
    0,3,2
  ))
  mesh.enableInstancing(
    new VertexAttribute("rect",         4),
    new VertexAttribute("rectRotation", 1),
    new VertexAttribute("color",        4),
    new VertexAttribute("borderRadius", 1),
    new VertexAttribute("borderWidth",  1),
    new VertexAttribute("borderColor",  4),
    new VertexAttribute("blur",         1),
    new VertexAttribute("glyph",        4),
    new VertexAttribute("transformCol0",4),
    new VertexAttribute("transformCol1",4),
    new VertexAttribute("transformCol2",4),
    new VertexAttribute("transformCol3",4),
    new VertexAttribute("boundary",     4)
  )

  val shader=Shader.builder()
    .vertex("assets/renderer/widgetRenderer2/vertex.glsl")
    .fragment("assets/renderer/widgetRenderer2/fragment.glsl")
    .build()
  def draw(drawable: Drawable): Unit = {

  }
}
