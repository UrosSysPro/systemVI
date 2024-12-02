package com.systemvi.noise

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.noise.{FractalNoise2d, Octave2, Perlin2d}
import com.systemvi.engine.renderers.TextureRenderer
import com.systemvi.engine.texture.{Format, Texture, TextureData}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.{Vector2f, Vector2i, Vector4f}

object Main extends Game(3, 3, 60, 800, 600, "Noise") {
  var textureRenderer: TextureRenderer = null
  var texture: Texture = null
  var camera: Camera3 = null

  override def setup(window: Window): Unit =
    val width = window.getWidth.toFloat
    val height = window.getHeight.toFloat
    camera = Camera3.builder2d()
      .size(width, height)
      .position(width / 2, height / 2)
      .build()
    texture = new Texture(window.getWidth, window.getHeight, Format.RGBA)
    redraw(window.getWidth, window.getHeight)
    textureRenderer = new TextureRenderer()

  override def loop(delta: Float): Unit =
    Utils.clear(Colors.green400, Buffer.COLOR_BUFFER)
    textureRenderer.view(camera.view)
    textureRenderer.projection(camera.projection)
    textureRenderer.draw(texture, 0, 0, texture.getWidth.toFloat, texture.getHeight.toFloat)
    textureRenderer.flush()

  override def resize(width: Int, height: Int): Boolean =
    texture.delete()
    texture = new Texture(width, height, Format.RGB32F)
    camera
      .position(width.toFloat / 2, height.toFloat / 2, 0)
      .orthographic(-width.toFloat / 2, width.toFloat / 2, -height.toFloat / 2, height.toFloat / 2, 0, 1)
      .update()
    redraw(width, height)
    true

  private def redraw(width: Int, height: Int): Unit = {
    val fractalNoise=FractalNoise2d.harmonicsPerlin(4)
    val n = 1
    val octaves = for (i <- 1 to n) yield
      val scale = Math.pow(2f, i).toFloat
      Octave2(
        noise = Perlin2d(Vector2i(100,100)),
//        noise = Voronoi2(Vector2i(10,10)),
        frequency = scale,
//        amplitude = 1f/scale,
        amplitude = 1f,
      )
    val textureData = new TextureData(width, height, Format.RGBA)
    val scale = 1f / 250f

    for (i <- 0 until width; j <- 0 until height)
      val point = new Vector2f(i.toFloat * scale, j.toFloat * scale)
//      var value = octaves.foldLeft(0f) {
//        case (acc, octave) =>
//          acc+octave.get(point)
//      }

//      value= value match
//        case x if x<0.1 => 0.01
//        case x if x<0.3 => 0.2
//        case x if x<0.5 => 0.5
//        case x if x<0.7 => 0.8
//        case _=>0.99

      val value=fractalNoise.get(point)
      
      value match
        case x if x > 1 => textureData.set(i, j, Colors.blue400)
        case x if x < 0 => textureData.set(i, j, Colors.red400)
        case x => textureData.set(i, j, new Vector4f(x, x, x, 1))

    texture.setData(textureData)
  }

  def main(args: Array[String]): Unit = run()
}
