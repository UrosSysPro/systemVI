package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

class Row(var children:Array[Widget]) extends StatelessWidget {
  override def build(): Widget = {
    null
  }
  override def calculateSize(maxParentSize: Vector2f): Vector2f = super.calculateSize(maxParentSize)
  override def calculatePosition(parentPosition: Vector2f): Unit = super.calculatePosition(parentPosition)
  override def draw(renderer: WidgetRenderer): Unit = super.draw(renderer)
  override def debugPrint(tabs: String): Unit = super.debugPrint(tabs)
}

object Row{
  def apply(children: Array[Widget]): Row = new Row(children)
}
