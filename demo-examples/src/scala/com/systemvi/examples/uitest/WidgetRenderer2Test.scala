package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.ui.WidgetRenderer2
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window


class WidgetRenderer2Test extends Game(3,3,60,800,600,"Widget renderer test"){
  var camera:Camera3=null
  var widgetRenderer:WidgetRenderer2=null
  override def setup(window: Window): Unit = {
    camera=Camera3.builder2d()
      .size(window.getWidth,window.getHeight)
      .position(window.getWidth/2,window.getHeight/2)
      .scale(1,-1)
      .build()
    widgetRenderer=new WidgetRenderer2()
  }
  override def loop(delta: Float): Unit = {
    Utils.clear(1,0,0,1,Buffer.COLOR_BUFFER)
  }
}
