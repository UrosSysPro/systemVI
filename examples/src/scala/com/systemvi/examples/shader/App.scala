package com.systemvi.examples.shader

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.{Matrix4f, Vector2i}

class App extends Game(4,6,60,800,600,"Shader"){
  private var vertexShader:Shader=null
  private var geometryShader:Shader=null
  private var vertexArray:VertexArray=null
  private var controller:CameraController3=null
  private val grid=new Vector2i(10,10)
  private val model =new Matrix4f().identity()
    .rotateXYZ(Math.PI.toFloat/2,0,0)
    .scale(10)

  override def setup(window: Window): Unit = {
    vertexArray=new VertexArray()

    vertexShader=Shader.builder()
      .vertex("assets/examples/shader/proceduralVertex/vertex.glsl")
      .fragment("assets/examples/shader/proceduralVertex/fragment.glsl")
      .build()

    geometryShader=Shader.builder()
      .fragment("assets/examples/shader/geometryShader/fragment.glsl")
      .vertex("assets/examples/shader/geometryShader/vertex.glsl")
      .geometry("assets/examples/shader/geometryShader/geometry.glsl")
      .build()

    controller=CameraController3.builder()
      .window(window)
      .camera(Camera3.builder3d().build())
      .aspect(window.getWidth.toFloat/window.getHeight.toFloat)
      .build()
    setInputProcessor(controller)
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.blue500,Buffer.COLOR_BUFFER,Buffer.DEPTH_BUFFER)
    Utils.enableDepthTest();
    //    Utils.enableLines(2)
    controller.update(delta)
    vertexArray.bind()

    vertexShader.use()
    vertexShader.setUniform("grid",grid)
    vertexShader.setUniform("view",controller.camera.view)
    vertexShader.setUniform("model",model)
    vertexShader.setUniform("projection",controller.camera.projection)
    vertexShader.drawArrays(Primitive.TRIANGLE_STRIP,(grid.x*2+2)*(grid.y-1))



    geometryShader.use()
    geometryShader.setUniform("grid",grid)
    geometryShader.setUniform("view",controller.camera.view)
    geometryShader.setUniform("model",model)
    geometryShader.setUniform("projection",controller.camera.projection)
    geometryShader.drawArrays(Primitive.TRIANGLE_STRIP,(grid.x*2+2)*(grid.y-1))
  }
}
