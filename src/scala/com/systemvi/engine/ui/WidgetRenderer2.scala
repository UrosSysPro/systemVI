package com.systemvi.engine.ui

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.model.{Mesh, VertexAttribute}
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.engine.texture.Texture
import com.systemvi.engine.ui.utils.context.DrawContext
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.ui.utils.font.Font
import org.joml.{Matrix4f, Vector4f}

case class Rect(var x:Float=0, var y:Float=0,var width:Float=0,var height:Float=0,var rotation:Float=0){
  def set(x:Float,y:Float,width:Float,height:Float,rotation:Float): Unit = {
    this.x=x
    this.y=y
    this.width=width
    this.height=height
    this.rotation=rotation
  }
}
case class Glyph(var left:Float= -1,var top:Float= -1,var right:Float= -1,var bottom:Float= -1){
  def set(left:Float,top:Float,right:Float,bottom:Float): Unit = {
    this.left=left
    this.right=right
    this.top=top
    this.bottom=bottom
  }
}
case class Border(var radius:Float=0,var width:Float=0,var color:Vector4f=new Vector4f())
case class Drawable(
                     rect:Rect=Rect(),
                     color:Vector4f=new Vector4f(),
                     border:Border=Border(),
                     var blur:Float=0,
                     boundary:Rect=Rect(),
                     glyph:Glyph=Glyph(),
                     transform:Matrix4f=new Matrix4f().identity()
                   ){
  def writeToArray(array:Array[Float],index:Int): Unit = {
    val offset=index*Drawable.size
    array(offset+0)=rect.x
    array(offset+1)=rect.y
    array(offset+2)=rect.width
    array(offset+3)=rect.height
    array(offset+4)=rect.rotation

    array(offset+5)=color.x
    array(offset+6)=color.y
    array(offset+7)=color.z
    array(offset+8)=color.w

    array(offset+9)=border.radius
    array(offset+10)=border.width
    array(offset+11)=border.color.x
    array(offset+12)=border.color.y
    array(offset+13)=border.color.z
    array(offset+14)=border.color.w

    array(offset+15)=blur

    array(offset+16)=glyph.left
    array(offset+17)=glyph.top
    array(offset+18)=glyph.right
    array(offset+19)=glyph.bottom

    val data:Array[Float]=Array.ofDim(16)
    transform.get(data)
    data.zipWithIndex.foreach{
      case (value,index)=>array(offset+20+index)=value
    }
    array(offset+36)=boundary.x
    array(offset+37)=boundary.y
    array(offset+38)=boundary.width
    array(offset+39)=boundary.height
  }
}
object Drawable{
  val size=40
}
class WidgetRenderer2(var camera:Camera3,var font:Font) {
  val mesh = new Mesh(
    new VertexAttribute("position",4)
  )
  val size=0.5f
  val drawable:Drawable=Drawable()
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

  val maxInstances=1000
  var instancesToDraw=0
  val instanceData:Array[Float]=Array.ofDim(Drawable.size*maxInstances)

  def draw(drawable: Drawable): Unit = {
    if(instancesToDraw>=maxInstances)flush()
    drawable.writeToArray(instanceData,instancesToDraw)
    instancesToDraw+=1
  }

  def rect(x:Float,y:Float,w:Float,h:Float,
           color:Vector4f,borderRadius:Float,
           blur:Float,clipRect:Vector4f,
           borderWidth:Float,borderColor:Vector4f,context:DrawContext): Unit = {
    drawable.rect.set(x+w/2,y+h/2,w,h,0)

    drawable.border.color.set(borderColor)
    drawable.border.width=borderWidth
    drawable.border.radius=borderRadius

    drawable.glyph.set(-1,-1,-1,-1)

    drawable.blur=blur

    drawable.boundary.set(clipRect.x,clipRect.y,clipRect.z,clipRect.w,0)

    drawable.transform.set(context.transform)

    drawable.color.set(color)
    draw(drawable)
  }
  def rect(x:Float,y:Float,w:Float,h:Float,color:Vector4f,borderRadius:Float,blur:Float,context: DrawContext):Unit={
    rect(x,y,w,h,
      color,borderRadius,
      blur,clipRect = new Vector4f(0,0,100000,100000),
      borderWidth = 0,borderColor = color,context
    )
  }
  def rect(x:Float,y:Float,w:Float,h:Float,color:Vector4f,context: DrawContext):Unit={
    rect(x,y,w,h,color,0,0,context)
  }
  def rect(x:Float,y:Float,w:Float,h:Float,color:Vector4f,borderRadius:Float,context: DrawContext):Unit={
    rect(x,y,w,h,color,borderRadius,1,context)
  }
  def circle(x:Float,y:Float,r:Float,color:Vector4f,context: DrawContext):Unit={
    rect(x-r,y-r,r*2,r*2,color,r,1,context)
  }

  def drawSymbol(symbol: Font#Symbol,x:Float,y:Float,scale:Float,color:Vector4f,context: DrawContext): Unit = {
    drawable.rect.set(
      x+(symbol.xoffset+symbol.width.toFloat/2)*scale,
      y+(symbol.yoffset+symbol.height.toFloat/2)*scale,
      symbol.width*scale,
      symbol.height*scale,
      0
    )

    drawable.border.color.set(Colors.white)
    drawable.border.width=0
    drawable.border.radius=0

    drawable.glyph.set(
      symbol.x.toFloat/font.texture.getWidth.toFloat,
      symbol.y.toFloat/font.texture.getHeight.toFloat,
      (symbol.x.toFloat+symbol.width)/font.texture.getWidth.toFloat,
      (symbol.y.toFloat+symbol.height)/font.texture.getHeight.toFloat
    )

    drawable.blur=0

    drawable.boundary.set(0,0,100000,100000,0)

    drawable.transform.set(context.transform)

    drawable.color.set(color)
    draw(drawable)
  }

  def flush():Unit={
    shader.use()
    shader.setUniform("view",camera.view)
    shader.setUniform("projection",camera.projection)
    font.texture.bind(0)
    shader.setUniform("fontTexture",0)
    mesh.bind()
    mesh.setInstanceData(instanceData)
    mesh.drawInstancedElements(2,instancesToDraw)
    instancesToDraw=0
  }
}
