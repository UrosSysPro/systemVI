package com.systemvi.uitests

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.renderers.TextureRenderer
import com.systemvi.engine.texture.{Texture, TextureRegion}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.ui.utils.font.Font
import com.systemvi.engine.ui.*
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.{Matrix4f, Vector4f}


class WidgetRenderer2Test extends Game(3,3,60,800,600,"Widget renderer test"){
  var camera:Camera3=null
  var widgetRenderer:WidgetRenderer2=null
  var font:Font=null
  var textureRenderer:TextureRenderer=null
  var regions:Array[TextureRegion]=null
  var time:Float=0
  var string:String="Hello World"
  var colors:Array[Vector4f]=Array(
    Colors.yellow300,Colors.yellow500,Colors.orange300,Colors.orange500,Colors.red300,
    Colors.red500,Colors.purple300,Colors.purple500,Colors.blue300,
    Colors.blue500,Colors.green300,Colors.green500
  )
  override def setup(window: Window): Unit = {
    camera=Camera3.builder2d()
      .size(window.getWidth.toFloat,window.getHeight.toFloat)
      .position(window.getWidth/2f,window.getHeight/2f)
      .scale(1,-1)
      .build()

    font=Font.load(
      "assets/font.PNG",
      "assets/font.json"
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
    val radius=Math.sin(time).toFloat*25+25
    time+=delta
    Utils.clear(0,0,1,1,Buffer.COLOR_BUFFER)
    Utils.enableBlending()
    var x=100
    string.zipWithIndex.foreach{case (c,index)=>
      val s=font.symbols.find(p=>p.id.toChar==c).get
      val r=regions(font.symbols.indexOf(s))
      val y=100
      textureRenderer.draw(r,x+s.xoffset,y+s.yoffset,s.width,s.height)
      widgetRenderer.draw(Drawable(
        blur=1,
        color = colors(index),
//        border=Border(10,4,Colors.blue500),
        glyph = Glyph(r.getLeft,r.getTop,r.getRight,r.getBottom),
        rect = Rect(
          x+s.width.toFloat/2+s.xoffset.toFloat,
          y+s.height.toFloat/2+s.yoffset.toFloat,
          s.width.toFloat,
          s.height.toFloat
        )
//        transform = new Matrix4f().identity().translate(x,y,0).rotateZ(time).translate(-x,-y,0)
      ))
      x+=s.xadvance
    }

    widgetRenderer.draw(Drawable(
      rect = Rect(400,350,200,100),
      border = Border(50,20,Colors.green700),
      color = new Vector4f(0),
      blur=1
//      transform = new Matrix4f().identity().translate(300,300,0).rotateZ(time).translate(-300,-300,0)
    ))
//    widgetRenderer.rect(100,300,50,60,Colors.amber600,20,1,new Vector4f(0),10,Colors.amber900,context = )
//    widgetRenderer.circle(500,500,100,Colors.orange300)
    textureRenderer.flush()
    widgetRenderer.flush()
    Utils.disableBlending()
  }
}
