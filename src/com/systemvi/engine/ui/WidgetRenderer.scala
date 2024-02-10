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
  val rects:Array[RoundedRect]=(0 until maxRectsToDraw).map((index:Int)=>RoundedRect(
    transofrm = new Matrix4f(),
    color = new Vector4f(),
    borderRadius = 0,
    blur = 0
  )).toArray

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
    rects(rectsToDraw).transofrm.identity().translate(x+w/2,y+h/2,0).scale(w,h,1)
    rects(rectsToDraw).color.set(color)
    rectsToDraw+=1
  }
  def roundedRect():Unit={

  }
  def flush():Unit={
    val matrixSize:Int=16
    val colorSize=4
    val borderRadiusSize=1
    val blurSize=1
    val rectSize=matrixSize+colorSize+borderRadiusSize+blurSize
    val data:Array[Float]=Array.ofDim(rectSize*rectsToDraw)
    for(i<-0 until rectsToDraw){
      val offset=i*rectSize
      rects(i).transofrm.get(data)
      for(j<-0 until 16){
        instanceData(offset+j)=data(j)
      }
      instanceData(offset+16+0)=rects(i).color.x
      instanceData(offset+16+1)=rects(i).color.y
      instanceData(offset+16+2)=rects(i).color.z
      instanceData(offset+16+3)=rects(i).color.w
      instanceData(offset+16+4)=rects(i).borderRadius
      instanceData(offset+16+5)=rects(i).blur
    }
    mesh.setInstanceData(instanceData)
    shader.use()
    shader.setUniform("view",camera.getView())
    shader.setUniform("projection",camera.getProjection())
    mesh.drawInstancedElements(2,rectsToDraw)
    rectsToDraw=0
  }
}