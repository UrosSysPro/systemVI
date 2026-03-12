package com.systemvi.ray_marching.opengl.buffer

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.ray_marching.opengl.*
import org.lwjgl.opengl.GL33.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL15.*
import scala.concurrent.ExecutionContext
import com.systemvi.ray_marching.opengl.utils.printThread

class Buffer[T : BufferTarget](val id: Int) {
  def bind():   Unit = glBindBuffer(summon[BufferTarget[T]].targetId, id)
  def unbind(): Unit = glBindBuffer(summon[BufferTarget[T]].targetId, 0)

  def upload(data: Array[Float]): Unit = {
    val bindTarget = summon[BufferTarget[T]].targetId
    bind()
    glBufferData(bindTarget,data,GL_STATIC_DRAW)
  }

  def upload(data: Array[Int]): Unit = {
    val bindTarget = summon[BufferTarget[T]].targetId
    bind()
    glBufferData(bindTarget, data, GL_STATIC_DRAW)
  }
}

object Buffer:
  def make[T : BufferTarget](window: GLFWWindow): Resource[IO, Buffer[T]] = Resource.make[IO,Buffer[T]]{
    IO{
      val id = glGenBuffers()
      Buffer[T](id)
    }.printThread.evalOn(window.ec)
  }{ buffer =>
    IO{
      buffer.unbind()
      glDeleteBuffers(buffer.id)
    }.evalOn(window.ec)
  }

sealed trait ArrayBuffer
sealed trait ElementBuffer

trait BufferTarget[T]:
  def targetId: Int

object BufferTarget {
  given BufferTarget[ArrayBuffer] = new BufferTarget[ArrayBuffer] {
    override def targetId: Int = GL_ARRAY_BUFFER
  }

  given BufferTarget[ElementBuffer] = new BufferTarget[ElementBuffer] {
    override def targetId: Int = GL_ELEMENT_ARRAY_BUFFER
  }
}



