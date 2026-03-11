package com.systemvi.ray_marching.test

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.engine
import com.systemvi.engine.shader.Primitive
import com.systemvi.ray_marching.opengl.*
import com.systemvi.ray_marching.opengl.KeyAction.*
import com.systemvi.ray_marching.opengl.buffer.*
import com.systemvi.ray_marching.opengl.shader.Shader
import com.systemvi.ray_marching.opengl.utils.BufferBit.*
import com.systemvi.ray_marching.opengl.utils.Utils
import com.systemvi.ray_marching.sdf.*
import com.systemvi.ray_marching.test.RenderPipeline.RayMarching
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

case class MouseState(x: Double, y: Double, dx: Double, dy: Double, captured: Boolean, pressedButtons: Set[MouseButton])

case class KeyboardState(pressedKeys: Set[Int])

case class GameState(
                      running: Ref[IO, Boolean],
                      lastFrameStart: Ref[IO, Double],
                      camera: Ref[IO,Camera],
                      mouseState: Ref[IO,MouseState],
                      keyboardState: Ref[IO,KeyboardState],
                      renderPipeline: RenderPipeline,
                    )

enum RenderPipeline {
  case RayMarching extends RenderPipeline
  case Mesh extends RenderPipeline
}

case class RayMarchingPipeline(
                                vertexArray: VertexArray,
                                arrayBuffer: Buffer[ArrayBuffer],
                                shader: Shader,
                              )
case class MeshPipeline(
                         vertexArray: VertexArray,
                         arrayBuffer: Buffer[ArrayBuffer],
                         elementBuffer: Buffer[ElementBuffer],
                         mesh: Mesh,
                         shader: Shader,
                       )
case class GameResources(
                     context: GLFWContext,
                     window: GLFWWindow,
                     rayMarchingPipeline: RayMarchingPipeline,
                     meshPipeline: MeshPipeline,
                    )

object Test extends IOApp.Simple {

  private def rayMarchingPipelineResource(context: GLFWContext) = for{
    vertexArray <- VertexArray.make(context)
    arrayBuffer <- Buffer.make[ArrayBuffer](context)
    vertexShader <- Resource.eval{IO{engine.utils.Utils.readInternal("vertex.glsl")}}
    fragmentShader <- Resource.eval{IO{engine.utils.Utils.readInternal("fragment.glsl")}}
    sdfGlsl <- Resource.eval(IO{sdf.toGlsl})
    shader <- Shader.make(vertexShader, fragmentShader.replace("???",sdfGlsl), context)
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
      }.evalOn(context.ec)
    }
  }yield RayMarchingPipeline(
    vertexArray,
    arrayBuffer,
    shader
  )

  private def meshPipelineResource(context: GLFWContext) = for{
    vertexArray <- VertexArray.make(context)
    arrayBuffer <- Buffer.make[ArrayBuffer](context)
    elementBuffer <- Buffer.make[ElementBuffer](context)
    vertexShader <- Resource.eval{IO{engine.utils.Utils.readInternal("mesh/vertex.glsl")}}
    fragmentShader <- Resource.eval{IO{engine.utils.Utils.readInternal("mesh/fragment.glsl")}}
    mesh <- Resource.eval(IO{SurfaceNets.sdfToMesh(sdf,Bounds(Vector3f(-200),Vector3f(200)),200)})
    _<-Resource.eval(IO.println(mesh.vertices.length))
    _<-Resource.eval(IO.println(mesh.indices.length))
    shader <- Shader.make(vertexShader, fragmentShader, context)
    _ <- Resource.eval[IO,Unit]{
      IO{
        vertexArray.bind()
        arrayBuffer.bind()
        arrayBuffer.upload(mesh.vertices.toArray)
        elementBuffer.bind()
        elementBuffer.upload(mesh.indices.toArray)
        vertexArray.configure(List(VertexAttribute("position",3)))
      }.evalOn(context.ec)
    }
  }yield MeshPipeline(
    vertexArray,
    arrayBuffer,
    elementBuffer,
    mesh,
    shader
  )

  private def resources(renderThreadName:String) = for{
    context <- GLFWContext.make(3,3,renderThreadName)
    window <- GLFWWindow.make(context,800,600,"window")
    rayMarchingPipeline <- rayMarchingPipelineResource(context)
    meshPipeline <- meshPipelineResource(context)
  } yield GameResources(context,window,rayMarchingPipeline,meshPipeline)

  private def gameState(pipeline: RenderPipeline) = for{
    running <- Ref.of[IO,Boolean](true)
    lastFrameStart <- Ref.of[IO,Double](0.0)
    camera <- Ref.of[IO,Camera](Camera(Vector3f(0,0,-300),800f/600f,2.2f,0,0))
    mouseState <- Ref.of[IO,MouseState](MouseState(0,0,0,0,false,Set.empty))
    keyboardState <- Ref.of[IO,KeyboardState](KeyboardState(Set.empty))
  } yield GameState(running,lastFrameStart,camera,mouseState,keyboardState,pipeline)

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

  private val targetFPS: Int = 165
  private val targetFrameTime: Double = 1000d / targetFPS

  private def loop(state:GameState, resources: GameResources): IO[Unit] = {
    val context = resources.context
    val window = resources.window
    state.running.get.flatMap {
      case false => IO.unit
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

  private def update(delta: Double, state:GameState, resources: GameResources): IO[Unit] = {
    val context = resources.context
    val window = resources.window
    val mouseSensitivity = 0.0001d
    val mouseInvert = -1.0d

    for{
      mouseState <- state.mouseState.get
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
        val forward = Vector3f(Math.sin(yaw).toFloat, 0f, Math.cos(yaw).toFloat).mul(1f / delta.toFloat*movementSpeed)
        val right = Vector3f(forward).rotateY(-Math.PI.toFloat / 2f)
        val up = Vector3f(0f,-1.0,0f).mul(1f/delta.toFloat*movementSpeed)

        if(keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_W)) position.add(forward)
        if(keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_S)) position.sub(forward)
        if(keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_D)) position.add(right)
        if(keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_A)) position.sub(right)
        if(keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_SPACE)) position.add(up)
        if(keyboardState.pressedKeys.contains(GLFW.GLFW_KEY_LEFT_SHIFT)) position.sub(up)

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
    }.evalOn(context.ec)

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
    }.evalOn(context.ec)

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
      _ <- state.renderPipeline match {
        case RayMarching => drawRayMarchingPipeline(camera,resources.rayMarchingPipeline)
        case RenderPipeline.Mesh => drawMeshPipeline(camera,resources.meshPipeline)
      }
    }yield ()
  }

  private def app(renderThreadName: String, pipeline: RenderPipeline): IO[Unit] = {
    resources(renderThreadName).use{ resources =>
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
        state <- gameState(pipeline)
        _ <- loop(state, resources)
      } yield ()
    }
  }

//  override def run: IO[Unit] = app("render-thread-1",RayMarching)
  override def run: IO[Unit] = app("render-thread-2",RenderPipeline.Mesh)
}

