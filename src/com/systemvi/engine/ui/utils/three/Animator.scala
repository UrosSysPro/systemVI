package com.systemvi.engine.ui.utils.three

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.animation.Animatable
import com.systemvi.engine.ui.widgets.StatefulWidget

class Animator {
  def animate(widget: Widget,delta:Float): Unit = {
    widget match {
      case statefulWidget: StatefulWidget=>
        statefulWidget.state match {
          case animatable: Animatable=>animatable.controllers.foreach{
            controller=>controller.update(delta)
          }
          case _=>
        }
        statefulWidget.getChildren().foreach{
          widget=>animate(widget,delta)
        }
      case widget: Widget=>widget.getChildren().foreach{
        child=>animate(child,delta)
      }
      case null=>
    }
  }
}
