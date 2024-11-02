package com.systemvi.shapes

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import org.joml.Matrix4f

trait Shape {
  def vertexData():Array[Float]
  def elementData():Array[Int]
}

class ShapeRenderer {
  val view = Matrix4f()
  val projection = Matrix4f()
  var shapes = List.empty[Shape]
  
  val vertexArray=VertexArray()
  val arrayBuffer=ArrayBuffer()
  val elementBuffer=ElementsBuffer()
  
  val shader=Shader.builder()
    .vertex("vertex.glsl")
    .fragment("fragment.glsl")
    .build()

  def view(mat: Matrix4f): Unit = view.set(mat)

  def projection(mat: Matrix4f): Unit = projection.set(mat)

  def draw(shape: Shape): Unit = shapes :+= shape
  
  def flush(): Unit = {
    val vertexData= shapes.flatMap(shape => shape.vertexData()).toArray
    
    val elementData = shapes.flatMap(shape => shape.elementData()).toArray
    
    vertexArray.bind()
    arrayBuffer.setData(vertexData)
    elementBuffer.setData(elementData)
    
    shader.use()
    shader.setUniform("view",view)
    shader.setUniform("projection",projection)
    shader.drawElements(Primitive.TRIANGLES,elementData.length/3,ElementsDataType.UNSIGNED_INT,3)
  }
}
