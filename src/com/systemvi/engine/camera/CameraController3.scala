package com.systemvi.engine.camera

import com.systemvi.engine.window.InputProcessor
import org.lwjgl.glfw.GLFW

class CameraController3 extends InputProcessor{
  var focused:Boolean=false
  var camera:Camera3=null
  override def keyDown(key: Int, scancode: Int, mods: Int): Boolean = {
    key match {
      case GLFW.GLFW_KEY_Q=>
      case _=>
    }
    true
  }
  override def keyUp(key: Int, scancode: Int, mods: Int): Boolean = {
    true
  }
  override def mouseDown(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    focused=true
    true
  }
  override def mouseUp(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    true
  }
  override def mouseMove(x: Double, y: Double): Boolean = {
    true
  }
  override def scroll(offsetX: Double, offsetY: Double): Boolean = {
    true
  }
  override def resize(width: Int, height: Int): Boolean = {
    true
  }
}
