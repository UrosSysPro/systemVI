package com.systemvi.paint

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.renderers.{ShapeRenderer, TextureRenderer}
import com.systemvi.engine.texture.{Format, FrameBuffer, Texture}
import com.systemvi.engine.window.Window
import org.joml.Vector2f

object Main extends Game(3,3,60,800,600,"paint"){
  var camera:Camera3=null
  var shapeRenderer:ShapeRenderer=null
  var frameBuffer:FrameBuffer=null
  var textureRenderer:TextureRenderer=null
  var colorTexture:Texture=null

  val mousePrev=new Vector2f(0,0)
  val mouseCurr=new Vector2f()
  var mouseDown=false

  override def setup(window: Window): Unit = {
    camera=Camera3.builder2d()
      .size(window.getWidth.toFloat,window.getHeight.toFloat)
      .position(window.getWidth.toFloat/2,window.getHeight.toFloat/2)
      .build()
    shapeRenderer=new ShapeRenderer()
    colorTexture=new Texture(window.getWidth,window.getHeight,Format.RGB32F)
    frameBuffer=FrameBuffer.builder()
      .color(colorTexture)
      .build()
    textureRenderer=new TextureRenderer()
  }
  override def loop(delta: Float): Unit = {
    frameBuffer.begin()

    frameBuffer.end()

    mousePrev.set(mouseCurr)
  }

  override def mouseDown(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    mouseDown=true
    true
  }

  override def mouseUp(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    mouseDown=false
    true
  }

  override def mouseMove(x: Double, y: Double): Boolean = {
    mouseCurr.set(x,y)
    true
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}
