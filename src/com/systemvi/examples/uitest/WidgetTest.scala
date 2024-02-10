package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Padding, Row}
import com.systemvi.engine.window.Window

class WidgetTest extends Game(3,3,60,800,600,"Widget test"){

  var widget:Widget=null
  var renderer:WidgetRenderer=null
  override def setup(window: Window): Unit = {
    widget=Container(
      child=Padding(
        padding=EdgeInsets.all(8.0f),
        child=Container(
          child=Row(
            children=Array(
              Padding(
                padding = EdgeInsets.all(8),
                child = Container()
              )
            )
          )
        )
      )
    )
    renderer=new WidgetRenderer(window);
  }

  override def loop(delta: Float): Unit = {
    widget.draw(renderer)
  }
}
