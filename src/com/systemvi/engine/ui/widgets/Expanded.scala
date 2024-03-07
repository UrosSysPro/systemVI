package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext

class Expanded(child:Widget,val flex:Float) extends StatelessWidget {
  override def build(context:BuildContext): Widget = child
}

object Expanded{
  def apply(child: Widget=null, flex:Float=1): Expanded = new Expanded(child, flex)
}