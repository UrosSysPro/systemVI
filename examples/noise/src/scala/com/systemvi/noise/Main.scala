package com.systemvi.noise

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.noise.{Perlin2, PerlinNoise}
import com.systemvi.engine.renderers.TextureRenderer
import com.systemvi.engine.texture.{Format, Texture, TextureData}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.{Vector2f, Vector2i, Vector4f}

object Main extends Game(3,3,60,800,600,"Noise"){
  var textureRenderer:TextureRenderer=null
  var texture:Texture=null
  var camera:Camera3=null

  override def setup(window: Window): Unit = {
    val width=window.getWidth.toFloat
    val height=window.getHeight.toFloat
    camera=Camera3.builder2d()
      .size(width,height)
      .position(width/2,height/2)
      .build()
    texture=new Texture(window.getWidth,window.getHeight,Format.RGBA)
    redraw(window.getWidth,window.getHeight)
    textureRenderer=new TextureRenderer()
  }
  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.green400,Buffer.COLOR_BUFFER)
    textureRenderer.view(camera.view)
    textureRenderer.projection(camera.projection)
    textureRenderer.draw(texture,0,0,texture.getWidth.toFloat,texture.getHeight.toFloat)
    textureRenderer.flush()
  }

  override def resize(width: Int, height: Int): Boolean = {
    texture.delete()
    texture=new Texture(width,height,Format.RGB32F)
    camera
      .position(width.toFloat/2,height.toFloat/2,0)
      .orthographic(-width.toFloat/2,width.toFloat/2,-height.toFloat/2,height.toFloat/2,0,1)
      .update()
    redraw(width, height)
    true
  }

  private def redraw(width:Int, height:Int): Unit = {
    val noise=Perlin2(new Vector2i(100,100),0)
    val scale=1f/300
    val textureData = new TextureData(width, height, Format.RGBA)
    for (i <- 0 until width; j <- 0 until height)
      noise.get(new Vector2f(i.toFloat*scale,j.toFloat*scale)) match{
        case value if value > 1 =>textureData.set(i,j,Colors.green400)
        case value if value < 0 =>textureData.set(i,j,Colors.red400)
        case value:Float=>textureData.set(i,j,new Vector4f(value,value,value,1.0f))
      }
    texture.setData(textureData)
  }
  def main(args: Array[String]): Unit = {
    run()
  }
}
