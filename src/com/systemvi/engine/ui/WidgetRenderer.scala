package com.systemvi.engine.ui

import com.systemvi.engine.camera.Camera
import com.systemvi.engine.model.{Mesh, VertexAttribute}
import com.systemvi.engine.shader.Shader
import com.systemvi.engine.window.Window
import org.joml.{Matrix4f, Vector4f}

// texture font oblici(kvadrat krug) senke

case class RoundedRect(transofrm:Matrix4f,color:Vector4f,borderRadius:Float,blur:Float){

};

class WidgetRenderer(window:Window){
  val shader:Shader=Shader.builder()
    .fragment("assets/renderer/widgetRenderer/fragment.glsl")
    .vertex("assets/renderer/widgetRenderer/vertex.glsl")
    .build()

  val camera:Camera=Camera.default2d(window);

  val mesh=new Mesh(
    new VertexAttribute("position",3)
  )
  mesh.setVertexData(Array[Float](
    -0.5f,-0.5f,0.0f,
     0.5f,-0.5f,0.0f,
     0.5f, 0.5f,0.0f,
    -0.5f, 0.5f,0.0f
  ))
  mesh.setIndices(Array[Int](
    0,1,2,
    0,2,3
  ))
  val maxRectsToDraw=1000
  var rectsToDraw=0
  val transofrms:Array[Matrix4f]=(0 until maxRectsToDraw)
    .map((index:Int)=>new Matrix4f()).toArray
  val colors:Array[Vector4f]= (0 until maxRectsToDraw)
    .map((index: Int) => new Vector4f()).toArray

  val instanceData:Array[Float]=Array.ofDim(maxRectsToDraw*16)
  mesh.enableInstancing(
    new VertexAttribute("col0",4),
    new VertexAttribute("col1",4),
    new VertexAttribute("col2",4),
    new VertexAttribute("col3",4),
    new VertexAttribute("color",4),
    new VertexAttribute("borderRadius",1),
    new VertexAttribute("blur",1)
  )

  def circle(x:Float,y:Float,r:Float,color:Vector4f):Unit={

  }
  def rect(x:Float,y:Float,w:Float,h:Float,color:Vector4f):Unit={
    transofrms(rectsToDraw).identity().translate(x,y,0).scale(w,h,1)
    rectsToDraw+=1
  }
  def flush():Unit={
    val matrixSize:Int=16
    val colorSize=4
    val borderRadiusSize=1
    val blurSize=1
    val data:Array[Float]=Array.ofDim(matrixSize+colorSize+borderRadiusSize+blurSize)
    for(i<-0 until rectsToDraw){
      transofrms(i).get(data)
      for(j<-0 until 16){
        instanceData(i*16+j)=data(j)
      }
//      instanceData(16+0)=
    }
    mesh.setInstanceData(instanceData)
    shader.use()
    shader.setUniform("view",camera.getView())
    shader.setUniform("projection",camera.getProjection())
    mesh.drawInstancedElements(3,rectsToDraw)
    rectsToDraw=0
  }
}