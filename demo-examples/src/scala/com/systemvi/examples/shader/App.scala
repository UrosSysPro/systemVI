package com.systemvi.examples.shader

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.Vector2i

class App extends Game(4,6,60,800,600,"Shader"){
  var shader:Shader=null
  var vertexArray:VertexArray=null
  var controller:CameraController3=null
  val grid=new Vector2i(4,4)

  override def setup(window: Window): Unit = {
    vertexArray=new VertexArray()
    shader=Shader.builder()
      .vertex("assets/examples/shader/proceduralVertex/vertex.glsl")
      .fragment("assets/examples/shader/proceduralVertex/fragment.glsl")
      .build()
    controller=CameraController3.builder()
      .window(window)
      .camera(Camera3.builder3d().build())
      .aspect(window.getWidth.toFloat/window.getHeight.toFloat)
      .build()
    setInputProcessor(controller)
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.blue500,Buffer.COLOR_BUFFER)
    Utils.enableLines(2)
    controller.update(delta)
    shader.use()
    vertexArray.bind()
    shader.setUniform("grid",grid)
    shader.setUniform("view",controller.camera.view)
    shader.setUniform("projection",controller.camera.projection)
    shader.drawArrays(Primitive.TIRANGLE_STRIP,(grid.x*2+1)*(grid.y-1))
  }
}
