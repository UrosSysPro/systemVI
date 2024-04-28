package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import org.joml.{Matrix4f, Vector2f}

class Transform(child:Widget=null,translate:Vector2f=new Vector2f(0),rotate:Float=0,scale:Float=0) extends StatelessWidget {
  override def build(context: BuildContext): Widget = child
  override def draw(context: DrawContext): Unit = super.draw(context)
}
object Transform{
  def translate(child:Widget,offset:Vector2f):Transform=new Transform(child = child,translate = offset)
  def scale(child:Widget,scale:Float):Transform=new Transform(child,scale=scale)
  def rotate(child:Widget,rotate:Float):Transform=new Transform(child,rotate = rotate)
}
