package com.systemvi.engine.utils

import com.systemvi.engine.window.Window
import org.joml.{Vector2f, Vector2i}
import org.lwjgl.glfw.GLFW.*


object MouseUtils {

  sealed trait MouseButton(val id: Int)

  object Right extends MouseButton(GLFW_MOUSE_BUTTON_RIGHT)

  object Left extends MouseButton(GLFW_MOUSE_BUTTON_LEFT)

  object Middle extends MouseButton(GLFW_MOUSE_BUTTON_MIDDLE)


  def getPosition(window: Window): Vector2f = {
    val x = Array.ofDim[Double](1)
    val y = Array.ofDim[Double](1)
    glfwGetCursorPos(window.getId, x, y)
    Vector2f(x(0).toFloat, y(0).toFloat)
  }

  def isButtonPressed(window: Window, mouseButton: MouseButton): Boolean = 
    glfwGetMouseButton(window.getId, mouseButton.id)==GLFW_PRESS
  
}
