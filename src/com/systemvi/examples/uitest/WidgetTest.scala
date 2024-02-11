package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import com.systemvi.engine.ui.widgets.{Column, Container, EdgeInsets, Expanded, Padding, Row, SizedBox}
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
        child=Column(
          children = (0 until 3).map(rowIndex=>
            Expanded(
              child=Row(
                children = (0 until 3).map(columnIndex=>
                  Expanded(
                    child=Container(color = new Vector4f(rowIndex/3f,columnIndex/3f,0.9f,1.0f)
                    )
                  )
                ).toArray
              )
            )
          ).toArray
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
