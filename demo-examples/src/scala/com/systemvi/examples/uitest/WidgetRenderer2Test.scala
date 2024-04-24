package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.renderers.TextureRenderer
import com.systemvi.engine.texture.{Texture, TextureRegion}
import com.systemvi.engine.ui.WidgetRenderer2
import com.systemvi.engine.ui.utils.font.Font
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window


class WidgetRenderer2Test extends Game(3,3,60,800,600,"Widget renderer test"){
  var camera:Camera3=null
  var widgetRenderer:WidgetRenderer2=null
//  var font:Font=null
  var textureRenderer:TextureRenderer=null
  var regions:Array[Array[TextureRegion]]=null

  override def setup(window: Window): Unit = {
    camera=Camera3.builder2d()
      .size(window.getWidth,window.getHeight)
      .position(window.getWidth/2,window.getHeight/2)
      .scale(1,-1)
      .build()
    widgetRenderer=new WidgetRenderer2()
//    font=new Font("assets/examples/widgetRenderer2Test/font.PNG","assets/examples/widgetRenderer2Test/font.json")
//    regions=TextureRegion.split(font.texture,50,50)
    textureRenderer=new TextureRenderer()
    textureRenderer.view(camera.view)
    textureRenderer.projection(camera.projection)
  }
  override def loop(delta: Float): Unit = {
    Utils.clear(1,0,0,1,Buffer.COLOR_BUFFER)
    Utils.enableBlending()
//    textureRenderer.draw(font.texture,0,0,font.texture.getWidth,font.texture.getHeight)
    textureRenderer.flush()
    Utils.disableBlending()
  }
}
