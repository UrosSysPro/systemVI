package com.systemvi.ray_marching.test

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.engine.shader.Primitive
import com.systemvi.ray_marching.opengl.*
import com.systemvi.ray_marching.opengl.buffer.{ArrayBuffer, Buffer, VertexArray, VertexAttribute}
import com.systemvi.ray_marching.opengl.shader.Shader
import com.systemvi.ray_marching.opengl.utils.BufferBit.{ColorBit, DepthBit}
import com.systemvi.ray_marching.opengl.utils.Utils
import com.systemvi.ray_marching.test.Test.resources
import org.joml.Vector4f
import org.lwjgl.opengl.GL11.*

import scala.concurrent.duration.*

case class GameState(
                      running: Ref[IO, Boolean],
                      lastFrameStart: Ref[IO, Double],
                    )

case class GameResources(
                     context:GLFWContext,
                     window: GLFWWindow,
                     vertexArray: VertexArray,
                     arrayBuffer: Buffer[ArrayBuffer],
                     shader: Shader,
                    )

object Test extends IOApp.Simple {
  private def resources = for{
    context <- GLFWContext.make(3,3)
    window <- GLFWWindow.make(context,800,600,"window")
    vertexArray <- VertexArray.make(context)
    arrayBuffer <- Buffer.make[ArrayBuffer](context)
    shader <- Shader.make(
      """
        |#version 330
        |layout(location=0) in vec3 position;
        |
        |void main(){
        | gl_Position = vec4(position,1.0);
        |}
        |""".stripMargin,
      """
        |#version 330
        |out vec4 FragColor;
        |
        |void main(){
        | FragColor = vec4(0.2,0.5,0.9,1.0);
        |}
        |""".stripMargin,
      context
    )
    _ <- Resource.eval[IO,Unit]{
      IO{
        vertexArray.bind()
        arrayBuffer.bind()
        vertexArray.configure(List(VertexAttribute("position",3)))
        arrayBuffer.upload(Array(
          0.5f, 0.5f, 0.0f,
          -0.5f, 0.5f, 0.0f,
          0.5f, -0.5f, 0.0f,
        ))
      }.evalOn(context.ec)
    }
  } yield GameResources(context,window,vertexArray,arrayBuffer,shader)

  private val targetFPS: Int =  20
  private val targetFrameTime: Double = 1000d / targetFPS

  private def loop(state:GameState, resources: GameResources): IO[Unit] = {
    val context = resources.context
    val window = resources.window
    state.running.get.flatMap {
      case false => IO.unit // Exit loop
      case true =>
        for
          lastFrameStart <- state.lastFrameStart.get
          startTime <- context.getTime
          _ <- input(state, resources)
          _ <- update(startTime - lastFrameStart, state, resources)
          _ <- render(startTime - lastFrameStart, state, resources)
          endTime <- context.getTime
          elapsed = endTime - startTime
          _ <- state.lastFrameStart.set(startTime)
          sleepTime = (targetFrameTime - elapsed).millis
          _ <- IO.sleep(sleepTime).whenA(sleepTime > Duration.Zero)
          _ <- loop(state, resources)
        yield ()
    }
  }

  private def input(state:GameState, resources: GameResources):IO[Unit] ={
    val context = resources.context
    val window = resources.window
    val shader = resources.shader
    val vertexArray = resources.vertexArray
    for {
      shouldClose <- IO{
        window.pollEvents()
        window.shouldClose()
      }.evalOn(context.ec)
      _ <- state.running.set(!shouldClose)
    } yield ()
  }

  private def update(delta: Double, state:GameState, resources: GameResources): IO[Unit] = {
    for{
      _ <- IO.unit
    }yield ()
  }

  private def render(delta: Double, state:GameState, resources: GameResources):IO[Unit] ={
    val context = resources.context
    val window = resources.window
    val shader = resources.shader
    val vertexArray = resources.vertexArray
    for{
      _ <- IO{
        Utils.clear(Vector4f(0.4f,0.1f,0.1f,1.0f),ColorBit,DepthBit)
        shader.use()
        vertexArray.bind()
        shader.drawArrays(Primitive.TRIANGLES,0,3)
        window.swapBuffers()
      }.evalOn(context.ec)
    }yield ()
  }

  override def run: IO[Unit] = {
    resources.use{ resources =>
      val window = resources.window
      for {
        _ <- IO{
          println(window.getRenderer)
          println(window.getVendor)
          println(window.getVersion)
          println(window.getGlslVersion)
          println(window.x)
          println(window.y)
        }.evalOn(resources.context.ec)
        running <- Ref.of[IO,Boolean](true)
        lastFrameStart <- Ref.of[IO,Double](-targetFrameTime)
        state = GameState(running,lastFrameStart)
        _ <- loop(state, resources)
      } yield ()
    }
  }
}

