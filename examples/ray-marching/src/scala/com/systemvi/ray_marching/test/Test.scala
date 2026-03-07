package com.systemvi.ray_marching.test

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.ray_marching.opengl.*
import com.systemvi.ray_marching.opengl.buffer.{ArrayBuffer, Buffer, VertexArray, VertexAttribute}
import com.systemvi.ray_marching.opengl.shader.Shader
import org.lwjgl.opengl.GL11.*

import scala.concurrent.duration.*

case class GameState(
                      running: Ref[IO, Boolean],
                      lastFrameStart: Ref[IO, Double],
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
  } yield (context,window,vertexArray,arrayBuffer,shader)

  private val targetFPS:Int =  20
  private val targetFrameTime: Double = 1000d / targetFPS

  private def loop(state:GameState, context:GLFWContext, window: GLFWWindow): IO[Unit] = {
      state.running.get.flatMap {
        case false => IO.unit // Exit loop
        case true =>
          for
            lastFrameStart <- state.lastFrameStart.get
            startTime <- context.getTime
            _ <- update(startTime - lastFrameStart, state, context, window)
            endTime <- context.getTime
            _ <- state.lastFrameStart.set(startTime)
            elapsed = endTime - startTime
            sleepTime = (targetFrameTime - elapsed).millis
            _ <- IO.sleep(sleepTime).whenA(sleepTime > Duration.Zero)
            _ <- loop(state, context, window)
          yield ()
    }

  }

  def update(delta: Double, state:GameState, context: GLFWContext, window: GLFWWindow): IO[Unit] = {
    given GLFWContext = context
    for{
      //input
      _ <- window.pollEvents
      //update
      shouldClose <- window.shouldClose
      _ <- state.running.set(!shouldClose)
      //draw
      _ <- IO{
        glClearColor(0.4f,0.1f,0.1f,1.0f)
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT|GL_STENCIL_BUFFER_BIT)
        state.shader.use()
        state.vertexArray.bind()
        glDrawArrays(GL_TRIANGLES,0,3)
      }.evalOn(context.ec)
      _ <- window.swapBuffers
    }yield ()
  }


  override def run: IO[Unit] = {
    resources.use{ (context, window,vertexArray,arrayBuffer,shader) =>
      for {
        _ <- IO.println(window.renderer)
        _ <- IO.println(window.vendor)
        _ <- IO.println(window.version)
        _ <- IO.println(window.glslVersion)
        _ <- IO{
          vertexArray.bind()
          arrayBuffer.bind()
          vertexArray.configure(List(VertexAttribute("position",3)))
          arrayBuffer.upload(Array(
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
          ))
        }.evalOn(context.ec)
        running <- Ref.of[IO,Boolean](true)
        lastFrameStart <- Ref.of[IO,Double](-targetFrameTime)
        state = GameState(running,lastFrameStart,vertexArray,arrayBuffer,shader)
        _ <- loop(state, context, window)
      } yield ()
    }
  }
}

