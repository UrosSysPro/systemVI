package com.systemvi.ray_marching.test

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.engine.camera.CameraController3
import com.systemvi.engine.shader.Primitive
import com.systemvi.ray_marching.opengl.*
import com.systemvi.ray_marching.opengl.KeyAction.Press
import com.systemvi.ray_marching.opengl.buffer.{ArrayBuffer, Buffer, VertexArray, VertexAttribute}
import com.systemvi.ray_marching.opengl.shader.Shader
import com.systemvi.ray_marching.opengl.utils.BufferBit.{ColorBit, DepthBit}
import com.systemvi.ray_marching.opengl.utils.Utils
import com.systemvi.ray_marching.test.Test.resources
import org.joml.{Vector2f, Vector3f, Vector4f}
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

import scala.concurrent.duration.*

case class Camera(
                  position: Vector3f,
                  aspect: Float,
                  fi: Float,
                  pitch: Float,
                  yaw: Float
                 )

case class MouseState(x: Double, y: Double, dx: Double, dy: Double, captured: Boolean, pressedButtons: Set[MouseButton])

case class KeyboardState(pressedKeys: Set[Int])

case class GameState(
                      running: Ref[IO, Boolean],
                      lastFrameStart: Ref[IO, Double],
                      camera: Ref[IO,Camera],
                      mouseState: Ref[IO,MouseState],
                      keyboardState: Ref[IO,KeyboardState],
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
        |
        |struct Camera{
        | float fi,aspect,yaw,pitch;
        | vec3 position;
        |};
        |
        |uniform Camera camera;
        |out vec4 FragColor;
        |
        |void main(){
        | FragColor = vec4(camera.pitch,camera.yaw,0.9,1.0);
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

  private def gameState = for{
    running <- Ref.of[IO,Boolean](true)
    lastFrameStart <- Ref.of[IO,Double](0.0)
    camera <- Ref.of[IO,Camera](Camera(Vector3f(),0,2.2f,0,0))
    mouseState <- Ref.of[IO,MouseState](MouseState(0,0,0,0,false,Set.empty))
    keyboardState <- Ref.of[IO,KeyboardState](KeyboardState(Set.empty))
  } yield GameState(running,lastFrameStart,camera,mouseState,keyboardState)

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
      events <- window.eventQueue.drain()
      _ <- events.traverse{
        case InputEvent.MouseButtonEvent(button, action) => state.mouseState.update{state=>MouseState(
          state.x,
          state.y,
          state.dx,
          state.dy,
          state.captured,
          if action == Press then state.pressedButtons + button else state.pressedButtons - button)
        }
        case InputEvent.MouseMove(x, y) =>  state.mouseState.update{state=>MouseState(
          x,
          y,
          x - state.x,
          y - state.y,
          state.captured,
          state.pressedButtons,
        )}
        case InputEvent.KeyEvent(key, action, mods) => state.keyboardState.update{state=>KeyboardState(
          if action == Press then state.pressedKeys + key else state.pressedKeys - key
        )}
        case _ => IO.unit
      }
    } yield ()
  }

  private def update(delta: Double, state:GameState, resources: GameResources): IO[Unit] = {
    val context = resources.context
    val window = resources.window
    val shader = resources.shader
    val vertexArray = resources.vertexArray
    val mouseSensitivity = 0.001d
    for{
      mouseState <- state.mouseState.get
      keyboardState <- state.keyboardState.get
      _ <- state.camera.update{camera =>
        val yaw = camera.yaw + mouseState.dx / delta * mouseSensitivity
        val pitch = camera.pitch + mouseState.dy / delta * mouseSensitivity
        val position = Vector3f(camera.position)
        if(keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_W)) position.add(Vector3f(-Math.sin(yaw).toFloat,0,Math.cos(yaw).toFloat))
        Camera(
          position,
          camera.aspect,
          camera.fi,
          pitch.toFloat,
          yaw.toFloat,
        )
      }
      _ <- state.mouseState.update{state => MouseState(
        state.x,
        state.y,
        0,
        0,
        state.captured,
        state.pressedButtons
      )}
    } yield ()
  }

  private def render(delta: Double, state:GameState, resources: GameResources):IO[Unit] ={
    val context = resources.context
    val window = resources.window
    val shader = resources.shader
    val vertexArray = resources.vertexArray

    for{
      camera <- state.camera.get
      _ <- IO{
        Utils.clear(Vector4f(0.4f,0.1f,0.1f,1.0f),ColorBit,DepthBit)
        shader.use()
        shader.setUniform("camera.yaw",camera.yaw)
        shader.setUniform("camera.pitch",camera.pitch)
        shader.setUniform("camera.fi",camera.fi)
        shader.setUniform("camera.aspect",camera.aspect)
        shader.setUniform("camera.position",camera.position)
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
          println(
            s"""
               |renderer: ${window.getRenderer}
               |vendor: ${window.getVendor}
               |version: ${window.getVersion}
               |glsl version: ${window.getGlslVersion}
               |x: ${window.x}
               |y: ${window.y}
               |width: ${window.width}
               |height: ${window.height}
               |""".stripMargin)
        }.evalOn(resources.context.ec)
        state <- gameState
        _ <- loop(state, resources)
      } yield ()
    }
  }
}

