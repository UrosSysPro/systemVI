package com.systemvi.engine.utils

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.widgets.{StatefulWidget, StatelessWidget}

class ThreeBuilder {

  def build(root:Widget,threePosition:String): Unit = root match {
    case null=>

    case widget:StatelessWidget=>
      widget.child=widget.build()
      widget.getChildren().zipWithIndex.foreach({
        case (child: Widget,index:Int)=>build(child,s"$threePosition/${child.getClass.getSimpleName}.$index")
        case (null,_:Int)=>
      })
    case widget:StatefulWidget=>
      //pronadjemo state
      //postaviti paremetre za state
      //build
      widget.state=widget.createState()
      widget.state.updateBeforeBuild(widget,threePosition,this)
      widget.child=widget.build()
      widget.getChildren().zipWithIndex.foreach({
        case (child: Widget,index:Int)=>build(child,s"$threePosition/${child.getClass.getSimpleName}.$index")
        case (null,_:Int)=>
      })
    case widget: Widget=>println(s"[BUILD ERROR] ${widget.getClass.getSimpleName} not a stateful or stateless widget")
  }
}
