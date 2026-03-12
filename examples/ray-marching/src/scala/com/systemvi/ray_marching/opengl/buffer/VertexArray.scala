package com.systemvi.ray_marching.opengl.buffer

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.ray_marching.opengl.{GLFWContext, GLFWWindow}
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL33.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL20.{glEnableVertexAttribArray, glVertexAttribPointer}
import org.lwjgl.opengl.GL30.{glBindVertexArray, glDeleteVertexArrays, glGenVertexArrays}
import com.systemvi.ray_marching.opengl.utils.printThread

import scala.concurrent.ExecutionContext

case class VertexAttribute(
                            name:String,
                            size:Int,
                          )

class VertexArray(val id: Int):
  def bind():   Unit = glBindVertexArray(id)
  def unbind(): Unit = glBindVertexArray(0)
  def configure(attrs: List[VertexAttribute]): Unit = {
    val vertexSize = attrs.foldLeft(0){(acc,attr)=>acc+attr.size}
    var pointer = 0
    val sizeOfFloat = 4
    for (i <- attrs.indices) {
      glVertexAttribPointer(i, attrs(i).size, GL_FLOAT, false, vertexSize * sizeOfFloat, pointer * sizeOfFloat)
      glEnableVertexAttribArray(i)
      pointer += attrs(i).size
    }
  }

object VertexArray:
  def make(window: GLFWWindow): Resource[IO, VertexArray] = Resource.make[IO,VertexArray]{
    IO{
      val id = glGenVertexArrays()
      VertexArray(id)
    }.printThread.evalOn(window.ec)
  }{ vertexArray =>
    IO{
      vertexArray.unbind()
      glDeleteVertexArrays(vertexArray.id)
    }.evalOn(window.ec)
  }

