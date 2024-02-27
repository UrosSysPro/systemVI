package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.widgets._
import com.systemvi.engine.ui.{Scene, Widget, WidgetRenderer}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.{Vector2f, Vector4f}

class WidgetTest extends Game(3,3,60,800,600,"Widget test"){

  //  var widget:Widget=null
  //  var renderer:WidgetRenderer=null
  var scene:Scene=null
  override def setup(window: Window): Unit = {
    scene=Scene(
      window=window,
      root=Container(
        color=new Vector4f(0.3f,0.6f,0.9f,1.0f),
        child=Padding(
          padding = EdgeInsets.all(150),
          child=SizedBox(
            size=new Vector2f(300,300),
            child=Container(
              color = new Vector4f(0.7f,0.5f,0.3f,1.0f)
            )
          )
        )
      )
    )
    //    widget= Container(
    //      color=new Vector4f(0.3f,0.6f,0.9f,1.0f),
    //      child=Padding(
    //        padding = EdgeInsets.all(150),
    //        child=SizedBox(
    //          size=new Vector2f(300,300),
    //          child=Container(
    //            color = new Vector4f(0.7f,0.5f,0.3f,1.0f)
    //          )
    //        )
    //      )
    //    )
    //    widget.calculateSize(new Vector2f(800,600))
    //    widget.calculatePosition(new Vector2f(0,0))
    //    widget.debugPrint("")
    //    renderer=new WidgetRenderer(window)
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(0,0,0,0,Buffer.COLOR_BUFFER)
    Utils.enableBlending()
    scene.draw()
    //    widget.draw(renderer)
    //    renderer.flush()
    Utils.disableBlending()
  }
}
