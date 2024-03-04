package com.systemvi.engine.ui.utils

import com.systemvi.engine.ui.Widget

class ThreeWalker(shouldVisit:Widget=>Boolean,process:Widget=>Unit) {
  def walk(widget:Widget): Unit = {
    process(widget)
    val children=widget.getChildren()
    for (child<-children){
      if(child!=null && shouldVisit(child))walk(child)
    }
  }
}
