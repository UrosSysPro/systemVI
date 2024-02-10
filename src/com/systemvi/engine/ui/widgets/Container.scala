package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.{Widget, WidgetRenderer}


class Container(var child:Widget) extends Widget{

  override def build(): Unit = {

  }

  override def draw(renderer: WidgetRenderer): Unit = {

  }
}

object Container{
  def apply(child: Widget=null): Container = new Container(child)
}

