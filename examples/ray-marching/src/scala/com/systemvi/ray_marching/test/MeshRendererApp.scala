package com.systemvi.ray_marching.test

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.engine
import com.systemvi.engine.shader.Primitive
import com.systemvi.ray_marching.opengl.{InputEvent, *}
import com.systemvi.ray_marching.opengl.CursorMode.{Captured, Disabled, Normal}
import com.systemvi.ray_marching.opengl.KeyAction.*
import com.systemvi.ray_marching.opengl.buffer.*
import com.systemvi.ray_marching.opengl.shader.Shader
import com.systemvi.ray_marching.opengl.utils.BufferBit.*
import com.systemvi.ray_marching.opengl.utils.Utils
import com.systemvi.ray_marching.sdf.*
import com.systemvi.ray_marching.sdf.mesh.{Bounds, MarchingCubes, Mesh, StlExporter, SurfaceNets}
import org.joml.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

import scala.concurrent.duration.*

case class MouseData(x:Double, y: Double, dx: Double, dy: Double)

case class InputState(pressedKeys: Ref[IO,Set[Int]], pressedButtons: Ref[IO,Set[MouseButton]], mouseData:Ref[IO,MouseData], captured: Ref[IO,Boolean])

case class MeshRendererAppState(targetFps: Ref[IO,Int], inputState: InputState, camera: Ref[IO,Camera])

case class MeshRendererAppResources(context: GLFWContext, window: GLFWWindow, vertexArray: VertexArray, shader: Shader, mesh: Mesh)

case class OpenGlStats(fps:Int = 0, frameTime: Duration = Duration.Zero, sleepTime: Duration = Duration.Zero, jitter: Duration = Duration.Zero)

case class CatsEffectStats()

case class FrameData(delta: Duration, state: MeshRendererAppState, sharedState: SharedState, resources: MeshRendererAppResources, lastFrameStats: OpenGlStats, mainThreadTasks: Ref[IO,List[IO[Unit]]])

class MeshRendererApp {

  val n = 10
  val sdf: SDF = Union(
    (for(i<-0 until n)
      yield {
        val angle = Math.PI.toFloat * 2 / n * i
        val x = Math.cos(angle) * 100f
        val y = 0f
        val z = Math.sin(angle) * 100f
        val r = 25f
        Sphere(r)
          .translate(Vector3f(x,y,z))
      }
      ).toList *
  )

  private def resources(context: GLFWContext) = for {
    ec <- RenderThreadPool.make("mesh-render-pool")
    window <- GLFWWindow.make(context,ec,800,600,"Mesh Renderer")
    vertexArray <- VertexArray.make(window)
    arrayBuffer <- Buffer.make[ArrayBuffer](window)
    elementBuffer <- Buffer.make[ElementBuffer](window)
    vertexShader <- Resource.eval{IO{engine.utils.Utils.readInternal("mesh/vertex.glsl")}}
    fragmentShader <- Resource.eval{IO{engine.utils.Utils.readInternal("mesh/fragment.glsl")}}
    //    mesh <- Resource.eval(IO{SurfaceNets.sdfToMesh(sdf,Bounds(Vector3f(-200),Vector3f(200)),50)})
    mesh <- Resource.eval(IO{MarchingCubes.sdfToMesh(sdf,Bounds(Vector3f(-200),Vector3f(200)),50)})
    shader <- Shader.make(vertexShader, fragmentShader, window)
    _ <- Resource.eval[IO,Unit]{
      IO{
        vertexArray.bind()
        //setup vertex data
        arrayBuffer.bind()
        arrayBuffer.upload(mesh.vertices.toArray)
        vertexArray.configure(List(VertexAttribute("position",3)))
        //setup element data
        elementBuffer.bind()
        elementBuffer.upload(mesh.indices.toArray)
      }.evalOn(window.ec)
    }
  } yield MeshRendererAppResources(context, window,vertexArray,shader,mesh)

  private def state(targetFps: Int): IO[MeshRendererAppState] = for{
    targetFps <- Ref.of[IO,Int](targetFps)
    pressedButtons <- Ref.of[IO,Set[MouseButton]](Set.empty)
    pressedKeys <- Ref.of[IO,Set[Int]](Set.empty)
    mouseData <- Ref.of[IO,MouseData](MouseData(0,0,0,0))
    captured <- Ref.of[IO,Boolean](false)
    camera <- Ref.of[IO,Camera](Camera(Vector3f(0,0,-300),800f/600f,2.2,0,0))
  } yield MeshRendererAppState(targetFps,InputState(pressedKeys,pressedButtons,mouseData,captured),camera)

  private def loop(state: MeshRendererAppState, sharedState: SharedState, resources: MeshRendererAppResources, lastFrameStart: Duration, lastFrameStats: OpenGlStats): IO[Unit] = {
    val context = resources.context
    val window = resources.window
    sharedState.running.get.flatMap {
      case false => IO.unit
      case true =>
        for
          frameStart <- IO.monotonic
          targetFps <- state.targetFps.get
          targetFrameTime = 1.second / targetFps
          delta = frameStart-lastFrameStart
          mainThreadTasks <- Ref.of[IO,List[IO[Unit]]](List.empty)
          frameData = FrameData(delta,state,sharedState,resources,lastFrameStats,mainThreadTasks)
          shouldClose <- IO{
            window.pollEvents()
            window.shouldClose()
          }.evalOn(context.ec)
          _ <- sharedState.running.set(!shouldClose)
          _ <- IO.cede
          _ <- input(frameData)
          _ <- update(frameData)
          _ <- render(frameData).evalOn(window.ec)
          _ <- frameData.mainThreadTasks.getAndSet(List.empty).flatMap{tasks => tasks.traverse{_.evalOn(context.ec)}}
          _ <- IO.cede
          frameEnd <- IO.monotonic
          elapsed = frameEnd - frameStart
          sleepTime = targetFrameTime - elapsed
          _ <- IO.sleep(sleepTime).whenA(sleepTime > Duration.Zero)
          afterSleep <- IO.monotonic
          actualSleepTime = afterSleep - frameEnd
          lastFrameStats = OpenGlStats((1000_000f/elapsed.toMicros).toInt, elapsed, actualSleepTime, actualSleepTime-sleepTime)
          _ <- loop(state, sharedState, resources, frameStart,lastFrameStats)
        yield ()
    }
  }

  private def input(frameData: FrameData) = {
    val inputState = frameData.state.inputState
    for{
      events <- frameData.resources.window.eventQueue.drain()
      _<-events.traverse{
        case InputEvent.MouseButtonEvent(mouseButton, action) => inputState.pressedButtons.update{ buttons => action match{
          case Press => buttons + mouseButton
          case Release => buttons - mouseButton
          case _ => buttons
        }}
        case InputEvent.MouseMove(x,y) => inputState.mouseData.update{data => MouseData(x,y,x-data.x,y-data.y)}
        case InputEvent.KeyEvent(key, action, mods) => inputState.pressedKeys.update{ keys => action match {
          case Press => keys + key
          case Release => keys - key
          case _ => keys
        }}
        case _ => IO.unit
      }
    }yield ()
  }

  private def update(frameData: FrameData) = {
    val mainThreadTasks = frameData.mainThreadTasks
    val window = frameData.resources.window
    val delta = frameData.delta.toMillis.toFloat / 1000f

    val mouseSensitivity = 0.0001f
    val speed = 0.1f
    val mouseInvert = -1f
    for{
      pressedKeys <- frameData.state.inputState.pressedKeys.get
      pressedButtons <- frameData.state.inputState.pressedButtons.get
      mouseData <- frameData.state.inputState.mouseData.getAndUpdate{_.copy(dx=0,dy=0)}
      captured <- frameData.state.inputState.captured.get

      _ <- mainThreadTasks.update{_:+IO{
        val stats = frameData.lastFrameStats
        val fps = f"fps: ${Math.min(999,stats.fps)}%3d"
        val x = f"x: ${mouseData.x}%03f"
        val y = f"y: ${mouseData.y}%03f"
        val frameTime = f"frameTime: ${stats.frameTime.toMillis}%3d.${stats.frameTime.toMicros % 1000}%03d ms"
        val jitter = f"jitter: ${stats.jitter.toMillis}%3d.${stats.jitter.toMicros % 1000}%03d ms"
        window.setTitle(f"$fps $frameTime $jitter $x $y")
      }}

      _ <- if pressedButtons.contains(MouseButton.Left) && !captured then for{
        _ <- frameData.state.inputState.captured.set(true)
        _ <- mainThreadTasks.update{_:+IO{window.setCursorMode(Disabled)}}
      }yield () else IO.unit

      _ <- if pressedKeys.contains(GLFW.GLFW_KEY_ESCAPE) && captured then for{
        _ <- frameData.state.inputState.captured.set(false)
        _ <- mainThreadTasks.update{_:+IO{window.setCursorMode(Normal)}}
      }yield () else IO.unit

      _ <- frameData.state.camera.update{ camera =>
        val yaw = camera.yaw + mouseData.dx / delta * mouseSensitivity * mouseInvert
        val pitch = Math.clamp(
          -Math.PI/2,
          Math.PI/2,
          camera.pitch + mouseData.dy / delta * mouseSensitivity * mouseInvert,
        )
        val position = Vector3f(camera.position)

        val forward = Vector3f(Math.sin(yaw).toFloat, 0f, Math.cos(yaw).toFloat).mul(1f / delta * speed)
        val right = Vector3f(forward).rotateY(-Math.PI.toFloat / 2f)
        val up = Vector3f(0f,-1.0,0f).mul(1f / delta * speed)

        if (pressedKeys.contains(GLFW.GLFW_KEY_W)) position.add(forward)
        if (pressedKeys.contains(GLFW.GLFW_KEY_S)) position.sub(forward)
        if (pressedKeys.contains(GLFW.GLFW_KEY_D)) position.add(right)
        if (pressedKeys.contains(GLFW.GLFW_KEY_A)) position.sub(right)
        if (pressedKeys.contains(GLFW.GLFW_KEY_SPACE)) position.add(up)
        if (pressedKeys.contains(GLFW.GLFW_KEY_LEFT_SHIFT)) position.sub(up)

        Camera(
          position,
          camera.aspect,
          camera.fi,
          pitch.toFloat,
          yaw.toFloat,
        )
      }.whenA(captured)
    } yield ()
  }

  private def render(frameData: FrameData) ={
    val shader = frameData.resources.shader
    val vertexArray = frameData.resources.vertexArray
    val window = frameData.resources.window
    val mesh = frameData.resources.mesh
    for {
      camera <- frameData.state.camera.get
      _ <- IO {
        Utils.clear(Vector4f(0.4f, 0.1f, 0.1f, 1.0f), ColorBit, DepthBit)
        shader.use()
        shader.setUniform("projection", Matrix4f().identity()
          .perspective(Math.PI.toFloat / 3, camera.aspect, 0.1f, 10000f)
        )
        shader.setUniform("view", Matrix4f().identity()
          .rotateX(-camera.pitch)
          .rotateY(-camera.yaw)
          .translate(Vector3f(camera.position))
        )
        vertexArray.bind()
        shader.drawElements(Primitive.TRIANGLES, mesh.indices.length)
        window.swapBuffers()
      }
    } yield ()
  }

  def run(context: GLFWContext, sharedState: SharedState, targetFps: Int): IO[Unit] = resources(context).use{ resources=>
    for {
      state <- state(targetFps)
      _ <- loop(state, sharedState, resources, Duration.Zero, OpenGlStats())
    } yield ()
  }
}
