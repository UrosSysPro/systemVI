package com.systemvi.ray_marching.test

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.engine
import com.systemvi.engine.shader.Primitive
import com.systemvi.ray_marching.opengl.*
import com.systemvi.ray_marching.opengl.CursorMode.{Captured, Disabled, Normal}
import com.systemvi.ray_marching.opengl.KeyAction.*
import com.systemvi.ray_marching.opengl.buffer.*
import com.systemvi.ray_marching.opengl.shader.Shader
import com.systemvi.ray_marching.opengl.utils.BufferBit.*
import com.systemvi.ray_marching.opengl.utils.{Utils, printThread}
import com.systemvi.ray_marching.sdf.*
import com.systemvi.ray_marching.sdf.mesh.{Bounds, MarchingCubes, Mesh, StlExporter, SurfaceNets}
import com.systemvi.ray_marching.test.RenderPipeline.RayMarching
import com.systemvi.ray_marching.test.Test.resources
import org.joml.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import scala.concurrent.duration.*

case class MeshRendererAppState(targetFps: Ref[IO,Int])

case class MeshRendererAppResources(context: GLFWContext, window: GLFWWindow)

case class OpenGlStats(fps:Int = 0, frameTime: Duration = Duration.Zero, sleepTime: Duration = Duration.Zero, jitter: Duration = Duration.Zero)

case class CatsEffectStats()

case class FrameData(delta: Duration, state: MeshRendererAppState, sharedState: SharedState, resources: MeshRendererAppResources, lastFrameStats: OpenGlStats, mainThreadTasks: Ref[IO,List[IO[Unit]]])

class MeshRendererApp {
  private def resources(context: GLFWContext) = for {
    ec <- RenderThreadPool.make("mesh-render-pool")
    window <- GLFWWindow.make(context,ec,800,600,"Mesh Renderer")
  } yield MeshRendererAppResources(context, window)

  private def state: IO[MeshRendererAppState] = for{
    targetFps <- Ref.of[IO,Int](165)
  } yield MeshRendererAppState(targetFps)

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
          _ <- input(frameData)
          _ <- update(frameData)
          _ <- render(frameData).evalOn(window.ec)
          _ <- frameData.mainThreadTasks.getAndSet(List.empty).flatMap{tasks => tasks.traverse{_.evalOn(context.ec)}}
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

  private def input(frameData: FrameData) = IO.unit

  private def update(frameData: FrameData) = {
    val mainThreadTasks = frameData.mainThreadTasks
    val window = frameData.resources.window
    for{
      _ <- mainThreadTasks.update{_:+IO{
        val stats = frameData.lastFrameStats
        val fps = Math.min(999,stats.fps)
        window.setTitle(f"fps: $fps%3d frameTime: ${stats.frameTime.toMillis}%3d.${stats.frameTime.toMicros % 1000}%03d ms jitter: ${stats.jitter.toMillis}%3d.${stats.jitter.toMicros % 1000}%03d ms")
      }}
    } yield ()
  }

  private def render(frameData: FrameData) = IO.unit

  def run(context: GLFWContext, sharedState: SharedState): IO[Unit] = resources(context).use{ resources=>
    for {
      state <- state
      _ <- loop(state, sharedState, resources, Duration.Zero, OpenGlStats())
    } yield ()
  }
}
