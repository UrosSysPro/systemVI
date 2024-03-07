package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector4f


class Container(child:Widget,val color:Vector4f) extends StatelessWidget{
  override def build(context:BuildContext): Widget = {
    return child
  }

  override def draw(context:DrawContext): Unit = {
    context.renderer.rect(position.x,position.y,size.x,size.y,color)
    super.draw(context)
    //post processing
  }
}

object Container{
  def apply(child: Widget=null,color:Vector4f=new Vector4f(0,0,0,0)): Container = new Container(child,color)
}

