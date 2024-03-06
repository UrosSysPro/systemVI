package com.systemvi.engine.ui.utils.three

import com.systemvi.engine.ui.Widget

abstract class ThreeWalker() {
  def walk(widget:Widget): Unit = {
    process(widget)
    val children=widget.getChildren()
    for (child<-children){
      if(child!=null && shouldVisit(child))walk(child)
    }
  }
  def process(widget: Widget):Unit
  def shouldVisit(widget: Widget):Boolean
}
