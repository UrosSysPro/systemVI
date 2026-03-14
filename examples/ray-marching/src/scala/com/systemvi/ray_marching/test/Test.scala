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
import com.systemvi.ray_marching.test.Test.resources
import org.joml.*
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

case class MouseState(x: Double, y: Double, dx: Double, dy: Double, var captured: Boolean, pressedButtons: Set[MouseButton])

case class KeyboardState(pressedKeys: Set[Int])

case class GameState(
                      running: Ref[IO, Boolean],
                      lastFrameStart: Ref[IO, Duration],
                      camera: Ref[IO,Camera],
                      mouseState: Ref[IO,MouseState],
                      keyboardState: Ref[IO,KeyboardState],
                    )

trait Pipeline
case class RayMarchingPipeline(
                                vertexArray: VertexArray,
                                arrayBuffer: Buffer[ArrayBuffer],
                                shader: Shader,
                              ) extends Pipeline
case class MeshPipeline(
                         vertexArray: VertexArray,
                         arrayBuffer: Buffer[ArrayBuffer],
                         elementBuffer: Buffer[ElementBuffer],
                         mesh: Mesh,
                         shader: Shader,
                       ) extends Pipeline

case class GameResources(
                     context: GLFWContext,
                     window: GLFWWindow,
                     pipeline: Pipeline,
                    )

case class SharedState(running: Ref[IO,Boolean])

object Test extends IOApp.Simple {
  private def executionContexts = for{
    mainEc <- RenderThreadPool.make("main")
    rayMarchingEc <- RenderThreadPool.make("ray-marching-render-pool")
    meshEc <- RenderThreadPool.make("mesh-render-pool")
  }yield (mainEc, rayMarchingEc, meshEc)

  private def rayMarchingPipelineResource(window: GLFWWindow) = for{
    vertexArray <- VertexArray.make(window)
    arrayBuffer <- Buffer.make[ArrayBuffer](window)
    vertexShader <- Resource.eval{IO{engine.utils.Utils.readInternal("vertex.glsl")}}
    fragmentShader <- Resource.eval{IO{engine.utils.Utils.readInternal("fragment.glsl")}}
    sdfGlsl <- Resource.eval(IO{sdf.toGlsl})
    shader <- Shader.make(vertexShader, fragmentShader.replace("???",sdfGlsl), window)
    _ <- Resource.eval[IO,Unit]{
      IO{
        vertexArray.bind()
        arrayBuffer.bind()
        vertexArray.configure(List(VertexAttribute("position",3)))
        arrayBuffer.upload(Array(
          1f,  1f, 0.0f,
          1f, -1f, 0.0f,
          -1f, -1f, 0.0f,

          -1f, -1f, 0.0f,
          -1f,  1f, 0.0f,
          1f,  1f, 0.0f,
        ))
      }.evalOn(window.ec)
    }
  }yield RayMarchingPipeline(
    vertexArray,
    arrayBuffer,
    shader
  )

  private def meshPipelineResource(window: GLFWWindow) = for{
    vertexArray <- VertexArray.make(window)
    arrayBuffer <- Buffer.make[ArrayBuffer](window)
    elementBuffer <- Buffer.make[ElementBuffer](window)
    vertexShader <- Resource.eval{IO{engine.utils.Utils.readInternal("mesh/vertex.glsl")}}
    fragmentShader <- Resource.eval{IO{engine.utils.Utils.readInternal("mesh/fragment.glsl")}}
//    mesh <- Resource.eval(IO{SurfaceNets.sdfToMesh(sdf,Bounds(Vector3f(-200),Vector3f(200)),50)})
    mesh <- Resource.eval(IO{MarchingCubes.sdfToMesh(sdf,Bounds(Vector3f(-200),Vector3f(200)),50)})
    _<-Resource.eval(IO.println(mesh.vertices.length))
    _<-Resource.eval(IO.println(mesh.indices.length))
    _<-Resource.eval(IO{StlExporter().exportToFile(mesh.vertices,mesh.indices,"test.stl")})
    shader <- Shader.make(vertexShader, fragmentShader, window)
    _ <- Resource.eval[IO,Unit]{
      IO{
        vertexArray.bind()
        arrayBuffer.bind()
        arrayBuffer.upload(mesh.vertices.toArray)
        elementBuffer.bind()
        elementBuffer.upload(mesh.indices.toArray)
        vertexArray.configure(List(VertexAttribute("position",3)))
      }.evalOn(window.ec)
    }
  }yield MeshPipeline(
    vertexArray,
    arrayBuffer,
    elementBuffer,
    mesh,
    shader
  )

  private def resources = for{
    mainEc <- RenderThreadPool.make("main-thread-pool")
    meshEc <- RenderThreadPool.make("mesh-render-pool")
    rayMarchingEc <- RenderThreadPool.make("ray-marching-render-pool")
    context <- GLFWContext.make(3,3,mainEc)
    meshWindow <- GLFWWindow.make(context,meshEc,800,600,"window")
    rayMarchingWindow <- GLFWWindow.make(context,rayMarchingEc,800,600,"window")
    meshPipeline <- meshPipelineResource(meshWindow)
    rayMarchingPipeline <- rayMarchingPipelineResource(rayMarchingWindow)
  } yield (GameResources(context,meshWindow,meshPipeline),GameResources(context,rayMarchingWindow,rayMarchingPipeline))

  private def gameState = for{
    running <- Ref.of[IO,Boolean](true)
    time<-IO.monotonic
    lastFrameStart <- Ref.of[IO,Duration](time)
    camera <- Ref.of[IO,Camera](Camera(Vector3f(0,0,-300),800f/600f,2.2f,0,0))
    mouseState <- Ref.of[IO,MouseState](MouseState(0,0,0,0,false,Set.empty))
    keyboardState <- Ref.of[IO,KeyboardState](KeyboardState(Set.empty))
  } yield GameState(running,lastFrameStart,camera,mouseState,keyboardState)

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
    ).toList
  )

  private val targetFPS: Int = 165
  private val targetFrameTime: Duration = (1000d / targetFPS).millis

  private def loop(state:GameState, resources: GameResources): IO[Unit] = {
    val context = resources.context
    val window = resources.window
    state.running.get.flatMap {
      case false => IO.unit
      case true =>
        for
          shouldClose <- IO{
            window.pollEvents()
            window.shouldClose()
          }.evalOn(context.ec)
          _ <- state.running.set(!shouldClose)
          lastFrameStart <- state.lastFrameStart.get
          startTime <- IO.monotonic
          _ <- input(state, resources).evalOn(window.ec)
          _ <- update(startTime - lastFrameStart, state, resources).evalOn(window.ec)
          _ <- render(startTime - lastFrameStart, state, resources).evalOn(window.ec)
          endTime <- IO.monotonic
          elapsed = endTime - startTime
          _ <- state.lastFrameStart.set(startTime)
          sleepTime = targetFrameTime - elapsed
          _ <- IO.sleep(sleepTime).whenA(sleepTime > Duration.Zero)
          _ <- loop(state, resources)
        yield ()
    }
  }

  private def input(state:GameState, resources: GameResources):IO[Unit] ={
    val context = resources.context
    val window = resources.window

    for {
      events <- window.eventQueue.drain()
      _ <- events.traverse{
        case InputEvent.MouseButtonEvent(button, action) => state.mouseState.update{state=>MouseState(
          state.x,
          state.y,
          state.dx,
          state.dy,
          state.captured,
          action match {
            case Press =>state.pressedButtons + button
            case Release => state.pressedButtons - button
            case _ => state.pressedButtons
          }
        )}
        case InputEvent.MouseMove(x, y) =>  state.mouseState.update{state=>MouseState(
          x,
          y,
          x - state.x,
          y - state.y,
          state.captured,
          state.pressedButtons,
        )}
        case InputEvent.KeyEvent(key, action, mods) => state.keyboardState.update{state=>KeyboardState(
          action match {
            case Press =>state.pressedKeys + key
            case Release => state.pressedKeys - key
            case _ => state.pressedKeys
          }
        )}
        case _ => IO.unit
      }
    } yield ()
  }

  private def update(deltaDuration: Duration, state:GameState, resources: GameResources): IO[Unit] = {
    val context = resources.context
    val window = resources.window
    val mouseSensitivity = 0.0001d
    val mouseInvert = -1.0d
    val delta = deltaDuration.toMillis.toDouble / 1000

    for{
      mouseState <- state.mouseState.get
      _<-IO{
        if(mouseState.pressedButtons.contains(MouseButton.Left) && !mouseState.captured){
          window.setCursorMode(Disabled)
          mouseState.captured = true
          println("capture")
        }
      }.evalOn(context.ec)
      keyboardState <- state.keyboardState.get
      _ <- state.camera.update{camera =>
        val yaw = camera.yaw + mouseState.dx / delta * mouseSensitivity * mouseInvert
        val pitch = Math.clamp(
          camera.pitch + mouseState.dy / delta * mouseSensitivity * mouseInvert,
          -Math.PI/2,
          Math.PI/2
        )
        val position = Vector3f(camera.position)

        val movementSpeed = 0.1f
        val forward = Vector3f(Math.sin(yaw).toFloat, 0f, Math.cos(yaw).toFloat).mul(1f / delta.toFloat * movementSpeed)
        val right = Vector3f(forward).rotateY(-Math.PI.toFloat / 2f)
        val up = Vector3f(0f,-1.0,0f).mul(1f/delta.toFloat*movementSpeed)

        if (keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_W)) position.add(forward)
        if (keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_S)) position.sub(forward)
        if (keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_D)) position.add(right)
        if (keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_A)) position.sub(right)
        if (keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_SPACE)) position.add(up)
        if (keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_LEFT_SHIFT)) position.sub(up)

        if(keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_ESCAPE) && mouseState.captured) {
          window.setCursorMode(CursorMode.Normal)
          mouseState.captured = false
          println("release")
        }

        if mouseState.captured then
          Camera(
            position,
            camera.aspect,
            camera.fi,
            pitch.toFloat,
            yaw.toFloat,
          )
        else
          camera
      }.evalOn(context.ec)
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

  private def render(deltaDuration: Duration, state:GameState, resources: GameResources):IO[Unit] ={
    val context = resources.context
    val window = resources.window
    val delta = deltaDuration.toMillis.toDouble / 1000

    def drawRayMarchingPipeline(camera: Camera,pipeline: RayMarchingPipeline) = IO{
      Utils.clear(Vector4f(0.4f,0.1f,0.1f,1.0f),ColorBit,DepthBit)
      pipeline.shader.use()
      pipeline.shader.setUniform("camera.yaw",camera.yaw)
      pipeline.shader.setUniform("camera.pitch",camera.pitch)
      pipeline.shader.setUniform("camera.fi",camera.fi)
      pipeline.shader.setUniform("camera.aspect",camera.aspect)
      pipeline.shader.setUniform("camera.position",camera.position)
      pipeline.shader.setUniform("camera.view",Matrix4f().identity()
        .translate(Vector3f(camera.position).mul(-1))
        .rotateY(camera.yaw)
        .rotateX(camera.pitch)
      )
      pipeline.vertexArray.bind()
      pipeline.shader.drawArrays(Primitive.TRIANGLES,0,6)
      window.swapBuffers()
    }

    def drawMeshPipeline(camera: Camera,pipeline: MeshPipeline) = IO{
      Utils.clear(Vector4f(0.4f,0.1f,0.1f,1.0f),ColorBit,DepthBit)
      pipeline.shader.use()
      pipeline.shader.setUniform("projection",Matrix4f().identity()
        .perspective(Math.PI.toFloat/3,camera.aspect,0.1f,10000f)
      )
      pipeline.shader.setUniform("view",Matrix4f().identity()
        .rotateX(-camera.pitch)
        .rotateY(-camera.yaw)
        .translate(Vector3f(camera.position))
      )
      pipeline.vertexArray.bind()
      pipeline.shader.drawElements(Primitive.TRIANGLES,pipeline.mesh.indices.length)
      window.swapBuffers()
    }

    for{
      camera <- state.camera.get
      _<-IO{
        val x=camera.position.x
        val y=camera.position.y
        val z=camera.position.z
        val yaw=camera.yaw
        val pitch=camera.pitch
        window.setTitle(s"x: $x y:$y z: $z yaw: $yaw pitch: $pitch")
      }.evalOn(context.ec)
      _ <- (resources.pipeline match {
        case pipeline:RayMarchingPipeline => drawRayMarchingPipeline(camera,pipeline)
        case pipeline:MeshPipeline => drawMeshPipeline(camera,pipeline)
      }).evalOn(window.ec)
    }yield ()
  }

  private def app(): IO[Unit] = {
    resources.use{ resources =>
      List(resources._1,resources._2).parTraverse {resources =>
        val window = resources.window
        for {
          _ <- IO {
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
          }.evalOn(window.ec)
          state <- gameState
          _ <- loop(state, resources)
        } yield ()
      }.void
    }
  }

  private def newApp = (for{
    mainEc <- RenderThreadPool.make("main-thread-pool")
    context <- GLFWContext.make(3,3,mainEc)
    sharedState <- Resource.eval(for{
      running <- Ref.of[IO,Boolean](true)
    } yield SharedState(running))
  } yield (context,sharedState)).use{ (context,sharedState) =>
    val app = MeshRendererApp()
    for{
      _ <- List(
        app.run(context, sharedState, 165, true),
//        app.run(context, sharedState, 60,true),
//        app.run(context, sharedState, 30),
      ).parSequence
    } yield ()
  }

  override def run: IO[Unit] = newApp
}

