package com.systemvi.generative_shaders

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.{Matrix4f, Vector2i}


class Main extends Game(4,6,60,800,600,"Shader"){
  private var vertexShader:Shader=null
  private var geometryShader:Shader=null
  private var tesselationShader:Shader=null
  private var vertexArray:VertexArray=null
  private var controller:CameraController3=null
  private val grid=new Vector2i(10,10)
  private val vertexModel =new Matrix4f().identity()
    .rotateXYZ(Math.PI.toFloat/2,0,0)
    .scale(10)
  private val geometryModel =new Matrix4f().identity()
    .translate(10,0,0)
    .rotateXYZ(Math.PI.toFloat/2,0,0)
    .scale(10)
  private val tesselationModel =new Matrix4f().identity()
    .translate(0,0,10)
    .rotateXYZ(Math.PI.toFloat/2,0,0)
    .scale(10)


  override def setup(window: Window): Unit = {
    vertexArray=new VertexArray()

    vertexShader=Shader.builder()
      .vertex("vertex/vertex.glsl")
      .fragment("vertex/fragment.glsl")
      .build()

    geometryShader=Shader.builder()
      .fragment("geometry/fragment.glsl")
      .vertex("geometry/vertex.glsl")
      .geometry("geometry/geometry.glsl")
      .build()

    tesselationShader=Shader.builder()
      .fragment("tesselation/fragment.glsl")
      .vertex("tesselation/vertex.glsl")
      .geometry("tesselation/geometry.glsl")
//      .tesselationControl("tesselation/tesselationControl.glsl")
//      .tesselationEvaluation("tesselation/tesselationEvaluation.glsl")
      .build()

    controller=CameraController3.builder()
      .window(window)
      .camera(Camera3.builder3d().build())
      .aspect(window.getWidth.toFloat/window.getHeight.toFloat)
      .build()
    setInputProcessor(controller)

//    Utils.setNumberOfPatchVertices(4)
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
    vertexShader.setUniform("model",vertexModel)
    vertexShader.setUniform("projection",controller.camera.projection)
    vertexShader.drawArrays(Primitive.TIRANGLE_STRIP,(grid.x*2+2)*(grid.y-1))

    geometryShader.use()
    Utils.enableLines(3)
    geometryShader.setUniform("grid",grid)
    geometryShader.setUniform("view",controller.camera.view)
    geometryShader.setUniform("model",geometryModel)
    geometryShader.setUniform("projection",controller.camera.projection)
    geometryShader.drawArrays(Primitive.POINTS,grid.x*grid.y)
    Utils.disableLines()

    tesselationShader.use()
    Utils.enableLines(3)
    tesselationShader.setUniform("grid",grid)
    tesselationShader.setUniform("view",controller.camera.view)
    tesselationShader.setUniform("model",tesselationModel)
    tesselationShader.setUniform("projection",controller.camera.projection)
    tesselationShader.drawArrays(Primitive.POINTS,grid.x*grid.y)
    Utils.disableLines()

    Utils.disableDepthTest()
  }
}

object Main{
  def main(args: Array[String]): Unit = {
    new Main().run()
  }
}