package com.systemvi.ray_marching.opengl.shader

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.engine.shader.Primitive
import com.systemvi.ray_marching.opengl.GLFWContext
import org.joml.{Matrix2f, Matrix3f, Matrix4f, Vector2f, Vector2i, Vector3f, Vector3i, Vector4f, Vector4i}
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL40.glUniform1d

import scala.concurrent.ExecutionContext

class Shader(val id: Int) {

  def use():   Unit = glUseProgram(id)
  def unuse(): Unit = glUseProgram(0)
  def drawArrays(primitive: Primitive,start:Int,count:Int): Unit = glDrawArrays(primitive.id,start,count)
  def setUniform(name: String, mat: Matrix4f): Unit = {
    val data = new Array[Float](16)
    val uniformId = glGetUniformLocation(id, name)
    glUniformMatrix4fv(uniformId, false, mat.get(data))
  }
  def setUniform(name: String, mat: Matrix3f): Unit = {
    val data = new Array[Float](9)
    val uniformId = glGetUniformLocation(id, name)
    glUniformMatrix3fv(uniformId, false, mat.get(data))
  }
  def setUniform(name: String, mat: Matrix2f): Unit = {
    val data = new Array[Float](4)
    val uniformId = glGetUniformLocation(id, name)
    glUniformMatrix2fv(uniformId, false, mat.get(data))
  }
  def setUniform(name: String, value: Vector4i): Unit = {
    val uniformId = glGetUniformLocation(id, name)
    glUniform4i(uniformId, value.x, value.y, value.z, value.w)
  }
  def setUniform(name: String, value: Vector3i): Unit = {
    val uniformId = glGetUniformLocation(id, name)
    glUniform3i(uniformId, value.x, value.y, value.z)
  }
  def setUniform(name: String, value: Vector2i): Unit = {
    val uniformId = glGetUniformLocation(id, name)
    glUniform2i(uniformId, value.x, value.y)
  }
  def setUniform(name: String, value: Int): Unit = {
    val uniformId = glGetUniformLocation(id, name)
    glUniform1i(uniformId, value)
  }
  def setUniform(name: String, value: Vector4f): Unit = {
    val uniformId = glGetUniformLocation(id, name)
    glUniform4f(uniformId, value.x, value.y, value.z, value.w)
  }
  def setUniform(name: String, value: Vector3f): Unit = {
    val uniformId = glGetUniformLocation(id, name)
    glUniform3f(uniformId, value.x, value.y, value.z)
  }
  def setUniform(name: String, value: Vector2f): Unit = {
    val uniformId = glGetUniformLocation(id, name)
    glUniform2f(uniformId, value.x, value.y)
  }
  def setUniform(name: String, value: Float): Unit = {
    val uniformId = glGetUniformLocation(id, name)
    glUniform1f(uniformId, value)
  }
  def setUniform(name: String, value: Double): Unit = {
    val uniformId = glGetUniformLocation(id, name)
    glUniform1d(uniformId, value)
  }

}
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
