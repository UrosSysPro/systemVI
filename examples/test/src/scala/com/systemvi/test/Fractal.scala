package com.systemvi.test

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer
import com.systemvi.engine.buffer.{ArrayBuffer, VertexArray}
import com.systemvi.engine.camera.CameraController3
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window

import scala.util.Random
import scala.util.control.Breaks

object Fractal extends Game(3, 3, 60, 800, 600, "Fractal") {

  case class Point(x: Float, y: Float,z:Float=0)

  type Screen = Array[Array[Char]]
  val width = 500
  val height = 200

  def drawTriangle(): Array[Float] = {
    val random = Random()
    val points = Array(
      Point(0, 1,0),
      Point(-1, -1,1),
      Point(1, -1,1),
      Point(0, -1,-1),
    )
    var p = Point(
      random.nextFloat(),
      random.nextFloat()
    )

    val vertexData=(for (i <- 0 until 50000) yield{
      val q = points(random.nextInt(points.length))
      p = Point((p.x + q.x) / 2, (p.y + q.y) / 2,(p.z+q.z)/2)
      p
    }).flatMap(p=>Array(p.x,p.y,p.z)).toArray
    vertexData
  }

  def drawHexagon(screen: Screen): Unit = {
    val random = Random()
    val points = (0 until 6).map { index =>
      val angle = index * Math.PI.toFloat / 3
      val c = Point(width.toFloat / 2, height.toFloat / 2)
      val l1 = height.toFloat / 2 - 1
      val l2 = width.toFloat / 2 - 1
      Point(
        c.x + Math.cos(angle).toFloat * l2,
        c.y + Math.sin(angle).toFloat * l1
      )
    }

    var p = Point(
      random.nextFloat() * width,
      random.nextFloat() * height
    )
    var prev = 0
    for (i <- 0 until 5000) {
      var current = random.nextInt(points.length)
      Breaks.breakable {
        while (true) {
          if (current != prev) Breaks.break()
          current = random.nextInt(points.length)
        }
      }
      prev = current
      val q = points(current)
      p = Point((p.x + q.x) / 2, (p.y + q.y) / 2)
      screen(p.x.toInt)(p.y.toInt) = '*'
    }
  }


  var shader:Shader=null
  var controller:CameraController3=null
  var vertexArray:VertexArray=null
  var arrayBuffer:ArrayBuffer=null

  override def setup(window: Window): Unit = {
    val screen: Screen = Array.ofDim(width, height)

    shader=Shader.builder()
      .fragment("assets/fragment.glsl")
      .vertex("assets/vertex.glsl")
      .build()
    vertexArray=VertexArray()
    arrayBuffer=ArrayBuffer()
    vertexArray.bind()
    arrayBuffer.bind()
    arrayBuffer.setVertexAttributes(Array(
      VertexAttribute("position",3)
    ))
    arrayBuffer.setData(drawTriangle())
    controller=CameraController3.builder()
      .window(window)
      .aspect(window.getWidth/window.getHeight.toFloat)
      .build()
    setInputProcessor(controller)
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.black,Buffer.COLOR_BUFFER,Buffer.DEPTH_BUFFER)
    controller.update(delta)
    Utils.enableDepthTest()
    shader.use()
    shader.setUniform("view",controller.camera.view)
    shader.setUniform("projection",controller.camera.projection)
    vertexArray.bind()
    shader.drawArrays(Primitive.POINTS,5000)
    Utils.disableDepthTest()
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}
