package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget

class Row(children:Array[Widget]) extends Widget{
  override def build(): Unit = {

  }
}

object Row{
  def apply(children: Array[Widget]): Row = new Row(children)
}
