package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Padding, SizedBox}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.{Vector2f, Vector4f}

class WidgetTest extends Game(3,3,60,800,600,"Widget test"){

  var widget:Widget=null
  var renderer:WidgetRenderer=null
  override def setup(window: Window): Unit = {
    widget=Container(
      color=new Vector4f(0.3f,0.6f,0.9f,1.0f),
      child=SizedBox(
        child=Padding(
          padding=EdgeInsets.all(20.0f),
          child=SizedBox(
            child=Container(
              color=new Vector4f(0.9f,0.6f,0.3f,1.0f),
              child=Padding(
                padding = EdgeInsets.all(50),
                child = Container(color = new Vector4f(1.0f,0.5f,0.75f,1.0f))
              )
            ),
            size=new Vector2f(200,200)
          )
        )
      )
    )
    widget.calculateSize(new Vector2f(800,600))
    widget.calculatePosition(new Vector2f(0,0))
    widget.debugPrint("")
    renderer=new WidgetRenderer(window)
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(0,0,0,0,Buffer.COLOR_BUFFER)
    widget.draw(renderer)
    renderer.flush()
  }
}
