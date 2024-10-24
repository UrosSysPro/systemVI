
package com.systemvi.uitests

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.utils.font.Font
import com.systemvi.engine.ui.widgets.*
import com.systemvi.engine.ui.{Scene, Widget, WidgetRenderer, utils}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.{InputMultiplexer, Window}
import org.joml.{Vector2f, Vector4f}

import scala.collection.mutable

class WidgetTest extends Game(3,3,60,800,600,"Widget test"){

  private var scene:Scene=null
  override def setup(window: Window): Unit = {
    WidgetTest.font=Font.load(
      "assets/font.PNG",
      "assets/font.json"
    )
    scene = Scene(
      initialWidth=window.getWidth.toFloat,
      initialHeight=window.getHeight.toFloat,
      root = App(),
      font = WidgetTest.font
    )
//    scene.root.debugPrint("")
    setInputProcessor(new InputMultiplexer(this, scene))
  }
  override def loop(delta: Float): Unit = {
    Utils.clear(0,0,0,0,Buffer.COLOR_BUFFER)
    scene.animate(delta)
    scene.resize(scene.width,scene.height)
    scene.draw()
  }

  override def resize(width: Int, height: Int): Boolean = {
    scene.resize(width,height)
  }
}

object WidgetTest{
  var font:Font=null
}