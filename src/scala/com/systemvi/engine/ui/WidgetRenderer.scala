package com.systemvi.engine.ui

import com.systemvi.engine.camera.Camera
import com.systemvi.engine.model.{Mesh, VertexAttribute}
import com.systemvi.engine.shader.Shader
import com.systemvi.engine.window.Window
import org.joml.{Matrix4f, Vector2f, Vector4f}
case class RoundedRect(transform:Matrix4f,
                       color:Vector4f,
                       var borderRadius:Float,
                       var blur:Float,
                       size:Vector2f,
                       var borderWidth:Float,
                       borderColor:Vector4f,
                       clipRect:Vector4f)
object RoundedRect{
  val size=33
  private val data:Array[Float]=Array.ofDim(16)
  def set(index:Int,array:Array[Float],rect:RoundedRect): Unit = {
    val offset=index*size
    rect.transform.get(data)
    //transform
    array(offset+0)=data(0)
    array(offset+1)=data(1)
    array(offset+2)=data(2)
    array(offset+3)=data(3)
    array(offset+4)=data(4)
    array(offset+5)=data(5)
    array(offset+6)=data(6)
    array(offset+7)=data(7)
    array(offset+8)=data(8)
    array(offset+9)=data(9)
    array(offset+10)=data(10)
    array(offset+11)=data(11)
    array(offset+12)=data(12)
    array(offset+13)=data(13)
    array(offset+14)=data(14)
    array(offset+15)=data(15)
    //color
    array(offset+16)=rect.color.x
    array(offset+17)=rect.color.y
    array(offset+18)=rect.color.z
    array(offset+19)=rect.color.w
    //border radius
    array(offset+20)=rect.borderRadius
    //blur
    array(offset+21)=rect.blur
    //size
    array(offset+22)=rect.size.x
    array(offset+23)=rect.size.y
    //borderWidth
    array(offset+24)=rect.borderWidth
    //borderColor
    array(offset+25)=rect.borderColor.x
    array(offset+26)=rect.borderColor.y
    array(offset+27)=rect.borderColor.z
    array(offset+28)=rect.borderColor.w
    //clipRect
    array(offset+29)=rect.clipRect.x
    array(offset+30)=rect.clipRect.y
    array(offset+31)=rect.clipRect.z
    array(offset+32)=rect.clipRect.w
  }
}
class WidgetRenderer(window:Window){

  private val shader:Shader=Shader.builder()
    .fragment("assets/renderer/widgetRenderer/fragment.glsl")
    .vertex("assets/renderer/widgetRenderer/vertex.glsl")
    .build()

  val camera:Camera=Camera.default2d(window);

  private val mesh=new Mesh(
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
  private val maxRectsToDraw=1000
  private var rectsToDraw=0
  private val rects:Array[RoundedRect]=(0 until maxRectsToDraw).map((index:Int)=>RoundedRect(
    transform = new Matrix4f(),
    color = new Vector4f(),
    borderRadius = 0,
    blur = 0,
    size = new Vector2f(),
    borderWidth = 0, borderColor = new Vector4f(), clipRect = new Vector4f()
  )).toArray

  val instanceData:Array[Float]=Array.ofDim(maxRectsToDraw*16)
  mesh.enableInstancing(
    new VertexAttribute("col0",4),
    new VertexAttribute("col1",4),
    new VertexAttribute("col2",4),
    new VertexAttribute("col3",4),
    new VertexAttribute("color",4),
    new VertexAttribute("borderRadius",1),
    new VertexAttribute("blur",1),
    new VertexAttribute("size",2),
    new VertexAttribute("borderWidth",1),
    new VertexAttribute("borderColor",4),
    new VertexAttribute("clipRect",4)
  )
  def rect(x:Float,y:Float,w:Float,h:Float,color:Vector4f,borderRadius:Float,blur:Float):Unit={
    if(rectsToDraw>maxRectsToDraw)flush()
    rects(rectsToDraw).transform.identity().translate(x+w/2,y+h/2,0).scale(w,h,1)
    rects(rectsToDraw).color.set(color)
    rects(rectsToDraw).size.set(w,h)
    rects(rectsToDraw).borderRadius=borderRadius
    rects(rectsToDraw).blur=blur
    rectsToDraw+=1
  }
  def rect(x:Float,y:Float,w:Float,h:Float,color:Vector4f):Unit={
    rect(x,y,w,h,color,0,0)
  }
  def rect(x:Float,y:Float,w:Float,h:Float,color:Vector4f,borderRadius:Float):Unit={
    rect(x,y,w,h,color,borderRadius,1)
  }
  def circle(x:Float,y:Float,r:Float,color:Vector4f):Unit={
    rect(x-r,y-r,r*2,r*2,color,r,1)
  }
  def flush():Unit={
//    val matrixSize:Int=16
//    val colorSize=4
//    val borderRadiusSize=1
//    val blurSize=1
//    val sizeSize=2
//    val rectSize=matrixSize+colorSize+borderRadiusSize+blurSize+sizeSize
//    val data:Array[Float]=Array.ofDim(rectSize*rectsToDraw)
//    for(i<-0 until rectsToDraw){
//      val offset=i*rectSize
//      rects(i).transform.get(data)
//      for(j<-0 until 16){
//        instanceData(offset+j)=data(j)
//      }
//      instanceData(offset+16+0)=rects(i).color.x
//      instanceData(offset+16+1)=rects(i).color.y
//      instanceData(offset+16+2)=rects(i).color.z
//      instanceData(offset+16+3)=rects(i).color.w
//      instanceData(offset+16+4)=rects(i).borderRadius
//      instanceData(offset+16+5)=rects(i).blur
//      instanceData(offset+16+6)=rects(i).size.x
//      instanceData(offset+16+7)=rects(i).size.y
//    }
    rects.zip(0 until rectsToDraw).foreach[Unit] {
      case (rect: RoundedRect, index: Int) => RoundedRect.set(index,instanceData,rect)
    }
    mesh.setInstanceData(instanceData)
    shader.use()
    shader.setUniform("view",camera.getView)
    shader.setUniform("projection",camera.getProjection)
    mesh.drawInstancedElements(2,rectsToDraw)
    rectsToDraw=0
  }
}