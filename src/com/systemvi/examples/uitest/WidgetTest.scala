package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Expanded, Padding, Row, SizedBox}
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
      child=Padding(
        padding=EdgeInsets.all(20),
        child=Row(
          children = Array(
            SizedBox(
              size = new Vector2f(100,Float.MaxValue),
              child = Container(color = new Vector4f(1,0,0,1))
            ),
            Expanded(
              child = Container(color = new Vector4f(0,1,0,1))
            ),
            SizedBox(
              size = new Vector2f(100,Float.MaxValue),
              child = Container(color = new Vector4f(1,0,0,1))
            )
          )
        ))
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
