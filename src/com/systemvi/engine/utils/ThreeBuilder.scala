package com.systemvi.engine.utils

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.widgets.{State, StatefulWidget, StatelessWidget}

class ThreeBuilder(var states:Map[String,State]) {

  private def getState(widget:StatefulWidget,threePosition:String):State=
    states.get(threePosition) match {
      case Some(state)=>state
      case None=>
        val state=widget.createState()
        state.init()
        states=states+(threePosition->state)
        state
    }


  def build(root:Widget,threePosition:String): Unit = root match {
    case null=>

    case widget:StatelessWidget=>
      widget.child=widget.build()
      widget.getChildren().zipWithIndex.foreach({
        case (child: Widget,index:Int)=>build(child,s"$threePosition/${child.getClass.getSimpleName}.$index")
        case (null,_:Int)=>
      })
    case widget:StatefulWidget=>
      widget.state=getState(widget,threePosition)
      widget.state.updateBeforeBuild(widget,threePosition,this)

      widget.child=widget.build()
      widget.getChildren().zipWithIndex.foreach({
        case (child: Widget,index:Int)=>build(child,s"$threePosition/${child.getClass.getSimpleName}.$index")
        case (null,_:Int)=>
      })
    case widget: Widget=>println(s"[BUILD ERROR] ${widget.getClass.getSimpleName} not a stateful or stateless widget")
  }
}
