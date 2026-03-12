package com.systemvi.ray_marching.opengl

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import cats.effect.unsafe.IORuntime
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.{GL, GLCapabilities}
import org.lwjgl.opengl.GL11.{GL_RENDERER, GL_VENDOR, GL_VERSION, glGetString, glViewport}
import org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack

import scala.util.*

enum KeyAction {
  case Press, Release, Repeat
}
object KeyAction {
  def fromGLFW(code: Int): KeyAction = code match {
    case GLFW_PRESS => Press
    case GLFW_RELEASE => Release
    case GLFW_REPEAT => Repeat
  }
}

sealed trait MouseButton
object MouseButton {
  case object Left extends MouseButton
  case object Right extends MouseButton
  case object Middle extends MouseButton
  case class Unknown(code: Int) extends MouseButton
  def fromGLFW(code: Int): MouseButton = code match{
    case GLFW_MOUSE_BUTTON_LEFT => MouseButton.Left
    case GLFW_MOUSE_BUTTON_RIGHT => MouseButton.Right
    case GLFW_MOUSE_BUTTON_MIDDLE => MouseButton.Middle
    case _ => MouseButton.Unknown(code)
  }
}

sealed trait InputEvent
object InputEvent{
  case class KeyEvent(key: Int, action: KeyAction, mods: Int) extends InputEvent
  case class MouseMove(x: Double, y: Double) extends InputEvent
  case class MouseButtonEvent(mouseButton: MouseButton, action: KeyAction) extends InputEvent
  case class MouseScroll(dx: Double, dy: Double) extends InputEvent
  case class WindowResize(window: Int, height: Int) extends InputEvent
  case class WindowMove(x: Int, y: Int) extends InputEvent
  case object WindowCloseRequest extends InputEvent
}

class EventQueue(private val ref:Ref[IO,List[InputEvent]]) {
  def push(event:InputEvent): Unit = ref.update(_:+event).unsafeRunSync()(using IORuntime.global)

  def drain(): IO[List[InputEvent]]= ref.getAndSet(List.empty)
}

object EventQueue {
  def make():IO[EventQueue] = Ref.of[IO,List[InputEvent]](List.empty).map(EventQueue(_))
}

enum CursorMode(val id:Int) {
  case Normal extends CursorMode(GLFW_CURSOR_NORMAL)
  case Hidden extends CursorMode(GLFW_CURSOR_HIDDEN)
  case Disabled extends CursorMode(GLFW_CURSOR_DISABLED)
  case Captured extends CursorMode(GLFW_CURSOR_CAPTURED)
}

class GLFWWindow(
                      val id: Long,
                      private var _width:Int,
                      private var _height: Int,
                      private var _title: String,
                      val capabilities: GLCapabilities,
                      val eventQueue: EventQueue,
                     ) {
  private var (_x: Int, _y: Int) = {
    Using(MemoryStack.stackPush()){ stack =>
      val xBuffer = stack.ints(1)
      val yBuffer = stack.ints(1)
      glfwGetWindowPos(id,xBuffer,yBuffer)
      (xBuffer.get(0),yBuffer.get(0))
    }.getOrElse((0,0))
  }

  def x: Int = _x

  def y: Int = _y

  def width: Int = _width

  def height: Int = _height

  def title: String = _title

  def makeCurrent(): Unit = {
    glfwMakeContextCurrent(id)
    glViewport(0, 0, width, height)
  }

  def setPosition(x: Int, y: Int): Unit = {
    this._x = x
    this._y = y
    glfwSetWindowPos(this.id,x,y)
  }

  def setSize(width: Int, height: Int): Unit = {
    this._width = width
    this._height = height
    glfwSetWindowSize(this.id, this.width, this.height)
    glViewport(0, 0, this.width, this.height)
  }

  def setTitle(title: String): Unit = {
    this._title = title
    glfwSetWindowTitle(this.id, this.title)
  }

  def swapBuffers(): Unit = {
    glfwSwapBuffers(this.id)
  }

  def pollEvents(): Unit = {
    glfwPollEvents()
  }

  def shouldClose(): Boolean = {
    glfwWindowShouldClose(this.id)
  }

  def getRenderer: String = glGetString(GL_RENDERER)

  def getVendor: String = glGetString(GL_VENDOR)

  def getVersion: String = glGetString(GL_VERSION)

  def getGlslVersion: String = glGetString(GL_SHADING_LANGUAGE_VERSION)

  def setCursorMode(mode: CursorMode):Unit = glfwSetInputMode(id, GLFW_CURSOR, mode.id)

  private def registerCallbacks(): Unit = {
    glfwSetKeyCallback(id, (_, key, _, action, mods) =>
      eventQueue.push(InputEvent.KeyEvent(key, KeyAction.fromGLFW(action), mods))
    )
    glfwSetCursorPosCallback(id, (_, x, y) =>
      eventQueue.push(InputEvent.MouseMove(x, y))
    )
    glfwSetMouseButtonCallback(id, (_, btn, action, _) =>
      eventQueue.push(InputEvent.MouseButtonEvent(MouseButton.fromGLFW(btn), KeyAction.fromGLFW(action)))
    )
    glfwSetScrollCallback(id, (_, dx, dy) =>
      eventQueue.push(InputEvent.MouseScroll(dx, dy))
    )
    glfwSetWindowSizeCallback(id, (_, w, h) =>
      _width = w
      _height = h
      eventQueue.push(InputEvent.WindowResize(w, h))
    )
    glfwSetWindowPosCallback(id, (_, x, y) =>
      _x = x
      _y = y
      eventQueue.push(InputEvent.WindowMove(x, y))
    )
    glfwSetWindowCloseCallback(id, _ =>
      eventQueue.push(InputEvent.WindowCloseRequest)
    )
  }
}

object GLFWWindow {
  def make(context: GLFWContext, width: Int, height: Int, title: String): Resource[IO, GLFWWindow] = for{
    eventQueue <- Resource.eval(EventQueue.make())
    window <- Resource.make {
    IO{
      val id = glfwCreateWindow(width, height, title, 0, 0)
      if(id==0){
        throw Exception("unable to create window")
      }
      glfwMakeContextCurrent(id)
      val capabilities = GL.createCapabilities
      glViewport(0, 0, width, height)
      val window = GLFWWindow(
        id,
        width,
        height,
        title,
        capabilities,
        eventQueue
      )
      window.registerCallbacks()
      window
    }.evalOn(context.ec)
  }{ window =>
    IO{
      glfwDestroyWindow(window.id)
    }.evalOn(context.ec)
  }
  }yield window
}
