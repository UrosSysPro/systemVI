package com.systemvi.ray_marching.opengl.shader

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.engine.shader.Primitive
import com.systemvi.ray_marching.opengl.GLFWContext
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*

import scala.concurrent.ExecutionContext

class Shader(val id: Int):
  def use():   Unit = glUseProgram(id)
  def unuse(): Unit = glUseProgram(0)
  def drawArrays(primitive: Primitive,start:Int,count:Int): Unit = glDrawArrays(primitive.id,start,count)

object Shader:
  def make(vert: String, frag: String, context: GLFWContext): Resource[IO, Shader] =
    Resource.make{
      IO {
        val v = compile(GL_VERTEX_SHADER, vert)
        val f = compile(GL_FRAGMENT_SHADER, frag)
        val p = glCreateProgram()
        glAttachShader(p, v)
        glAttachShader(p, f)
        glLinkProgram(p)
        glDeleteShader(v)
        glDeleteShader(f)
        Shader(p)
      }.evalOn(context.ec)
    }{p =>
      IO{
        glDeleteProgram(p.id)
      }.evalOn(context.ec)
    }

  private def compile(tpe: Int, src: String): Int =
    val id = glCreateShader(tpe)
    glShaderSource(id, src)
    glCompileShader(id)
    if glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE then
      throw RuntimeException(glGetShaderInfoLog(id))
    id
