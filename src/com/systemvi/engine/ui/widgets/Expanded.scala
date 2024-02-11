package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget

class Expanded(child:Widget,val flex:Float) extends StatelessWidget {
  override def build(): Widget = child
  override def debugPrint(tabs: String): Unit = {
    println(s"$tabs Expanded size: ${size.x} ${size.y} position: ${position.x} ${position.y}")
    super.debugPrint(tabs)
  }
}

object Expanded{
  def apply(child: Widget=null, flex:Float=1): Expanded = new Expanded(child, flex)
}