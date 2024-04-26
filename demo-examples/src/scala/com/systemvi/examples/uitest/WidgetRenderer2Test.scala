package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.renderers.TextureRenderer
import com.systemvi.engine.texture.{Texture, TextureRegion}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.ui.{Border, Drawable, Rect, WidgetRenderer2}
import com.systemvi.engine.ui.utils.font.Font
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window


class WidgetRenderer2Test extends Game(3,3,60,800,600,"Widget renderer test"){
  var camera:Camera3=null
  var widgetRenderer:WidgetRenderer2=null
  var font:Font=null
  var textureRenderer:TextureRenderer=null
  var regions:Array[TextureRegion]=null
  override def setup(window: Window): Unit = {
    camera=Camera3.builder2d()
      .size(window.getWidth,window.getHeight)
      .position(window.getWidth/2,window.getHeight/2)
      .scale(1,-1)
      .build()

    font=Font.load(
      "assets/examples/widgetRenderer2Test/font.PNG",
      "assets/examples/widgetRenderer2Test/font.json"
    )
    regions=Array.ofDim(font.symbols.length)
    font.symbols.zipWithIndex.foreach{
      case (symbol:Font#Symbol,index:Int)=> regions(index)=new TextureRegion(
        font.texture,symbol.x,symbol.y,symbol.width,symbol.height
      )
    }
    textureRenderer=new TextureRenderer()
    textureRenderer.view(camera.view)
    textureRenderer.projection(camera.projection)
    widgetRenderer=new WidgetRenderer2(camera,font)
  }
  override def loop(delta: Float): Unit = {
    Utils.clear(0.4f,0,0,1,Buffer.COLOR_BUFFER)
    Utils.enableBlending()
    val scale=2
    val r=regions(font.symbols.indexOf(font.symbols.find(p=>p.id.toChar=='/').get))
    textureRenderer.draw(r,100,100,r.width/scale,r.height/scale)
    textureRenderer.flush()
    widgetRenderer.draw(Drawable(
      rect = Rect(400,300,150,200,(Math.PI/12).toFloat),
      color = Colors.green500,
      border = Border(20,10,Colors.green700),
      glyph = Rect(r.getTop,r.getLeft,r.getBottom,r.getRight)
    ))
    widgetRenderer.draw(Drawable(
      rect = Rect(100,100,100,100,0),
      color = Colors.green500,
      border = Border(20,10,Colors.green700),
      glyph = Rect(0,0,1,1),
      blur = 10
    ))
    widgetRenderer.flush()
    Utils.disableBlending()
  }
}
