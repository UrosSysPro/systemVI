package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.widgets._
import com.systemvi.engine.ui.{Scene, Widget, WidgetRenderer}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.{Vector2f, Vector4f}

class WidgetTest extends Game(3,3,60,800,600,"Widget test"){

  private var scene:Scene=null
  override def setup(window: Window): Unit = {
    scene=Scene(
      window=window,
      root=Container(
        color=new Vector4f(0.3f,0.6f,0.9f,1.0f),
        child=Padding(
          padding = EdgeInsets.all(150),
          child=SizedBox(
            size=new Vector2f(300,300),
            child=Row(
              children = Array(
                GestureDetector(
                  child=Switch(true),
                  mouseDown = (button,mod,x,y)=>{
                    println("klik na prvi switch")
                    return true
                  }
                ),
                SizedBox(size = new Vector2f(20,0)),
                GestureDetector(
                  child=Switch(false),
                  mouseDown = (button,mod,x,y)=>{
                    println("klik na drugi switch")
                    return true
                  }
                ),
                SizedBox(size = new Vector2f(20,0))
              )
            )
          )
        )
      )
    )
    setInputProcessor(scene)
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(0,0,0,0,Buffer.COLOR_BUFFER)
    scene.draw()
  }
}
