package com.systemvi.generative_shaders

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.{VertexArray}
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.{Matrix4f, Vector2i}


class Main extends Game(4,1,60,800,600,"Shader"){
  private var vertexShader:Shader=null
  private var geometryShader:Shader=null
  private var tesselationShader:Shader=null
  private var combinedShader:Shader=null

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

  private val combinedModel =new Matrix4f().identity()
    .translate(10,0,10)
    .rotateXYZ(Math.PI.toFloat/2,0,0)
    .scale(10)


  override def setup(window: Window): Unit = {
    vertexArray=new VertexArray()
    vertexArray.bind()

    vertexShader=Shader.builder()
      .vertex("vertex/vertex.glsl")
      .fragment("vertex/fragment.glsl")
      .build()

    geometryShader=Shader.builder()
      .fragment("geometry/fragment.glsl")
      .vertex("geometry/vertex.glsl")
      .geometry("geometry/geometry.glsl")
      .build()

    Utils.setNumberOfPatchVertices(4)
    tesselationShader=Shader.builder()
      .fragment("tesselation/fragment.glsl")
      .vertex("tesselation/vertex.glsl")
      .tesselationControl("tesselation/tesselationControl.glsl")
      .tesselationEvaluation("tesselation/tesselationEvaluation.glsl")
      .build()

    combinedShader=Shader.builder()
      .fragment("combined/fragment.glsl")
      .vertex("combined/vertex.glsl")
      .tesselationControl("combined/tesselationControl.glsl")
      .tesselationEvaluation("combined/tesselationEvaluation.glsl")
      .geometry("combined/geometry.glsl")
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
    controller.update(delta)
    vertexArray.bind()

    Utils.enableLines(3)
    vertexShader.use()
    vertexShader.setUniform("grid",grid)
    vertexShader.setUniform("view",controller.camera.view)
    vertexShader.setUniform("model",vertexModel)
    vertexShader.setUniform("projection",controller.camera.projection)
    vertexShader.drawArrays(Primitive.TRIANGLE_STRIP,(grid.x*2+2)*(grid.y-1))
    Utils.disableLines()

    Utils.enableLines(3)
    geometryShader.use()
    geometryShader.setUniform("grid",grid)
    geometryShader.setUniform("view",controller.camera.view)
    geometryShader.setUniform("model",geometryModel)
    geometryShader.setUniform("projection",controller.camera.projection)
    geometryShader.drawArrays(Primitive.POINTS,grid.x*grid.y)
    Utils.disableLines()

    Utils.enableLines(3)
    tesselationShader.use()
    tesselationShader.setUniform("view",controller.camera.view)
    tesselationShader.setUniform("model",tesselationModel)
    tesselationShader.setUniform("projection",controller.camera.projection)
    tesselationShader.setUniform("cameraPosition",controller.camera.position)
    tesselationShader.drawArrays(Primitive.PATCHES,4)
    Utils.disableLines()

    Utils.enableLines(3)
    combinedShader.use()
    combinedShader.setUniform("view",controller.camera.view)
    combinedShader.setUniform("model",combinedModel)
    combinedShader.setUniform("projection",controller.camera.projection)
    combinedShader.setUniform("cameraPosition",controller.camera.position)
    combinedShader.drawArrays(Primitive.PATCHES,4)
    Utils.disableLines()

    Utils.disableDepthTest()
  }
}

object Main{
  def main(args: Array[String]): Unit = {
    new Main().run()
  }
}