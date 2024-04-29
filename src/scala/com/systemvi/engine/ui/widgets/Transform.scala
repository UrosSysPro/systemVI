package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import org.joml.{Matrix4f, Vector2f}

class Transform(child:Widget=null,translate:Vector2f=new Vector2f(0),rotate:Float=0,scale:Vector2f=new Vector2f(1)) extends StatelessWidget {
  override def build(context: BuildContext): Widget = child
  override def draw(context: DrawContext): Unit = {
    context.transform
      .translate(translate.x,translate.y,0)
      .translate(position.x+size.x/2,position.y+size.y/2,0).rotateZ(rotate).scale(scale.x,scale.y,1).translate(-position.x-size.x/2,-position.y-size.y/2,0)
    super.draw(context)
    context.transform
      .translate(position.x+size.x/2,position.y+size.y/2,0).scale(1/scale.x,1/scale.y,1).rotateZ(-rotate).translate(-position.x-size.x/2,-position.y-size.y/2,0)
      .translate(-translate.x,-translate.y,0)
  }
}
object Transform{
  def apply(child: Widget, translate: Vector2f, rotate: Float, scale: Vector2f): Transform = new Transform(child, translate, rotate, scale)
  def translate(child:Widget,offset:Vector2f):Transform=new Transform(child = child,translate = offset)
  def scale(child:Widget,scale:Vector2f):Transform=new Transform(child,scale=scale)
  def rotate(child:Widget,rotate:Float):Transform=new Transform(child,rotate = rotate)
}
