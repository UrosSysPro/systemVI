package com.systemvi.particle

import com.systemvi.engine.application.{Application, Game}
import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import com.systemvi.particle.Particles.random
import org.joml.{Vector2f, Vector2i, Vector4f}
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL

import scala.util.Random

case class Particle(position: Vector2f, acceleration: Vector2f, var life: Float, var lifeSpan: Float, color: Vector4f) {
  def restart(mouse: Vector2f, maxSpeed: Float, minSpeed: Float, random: Random): Unit = {
    val angle = random.nextFloat() * Math.PI * 2
    val speed = random.nextFloat() * (maxSpeed - minSpeed) + minSpeed
    acceleration.set(Math.cos(angle).toFloat, Math.sin(angle).toFloat).mul(speed)
    position.set(mouse)
    life = 0
    lifeSpan = random.nextFloat() * 5
  }
}

object Particles extends Game(3, 3, 60, 800, 600, "Triangle") {
  var vertexArray: VertexArray = null
  var arrayBuffer: ArrayBuffer = null
  var elementsBuffer: ElementsBuffer = null
  var shader: Shader = null
  var camera: Camera3 = null
  var particles: Array[Particle] = null
  val random = Random()
  val mouse = Vector2f()
  val n = 5000
  val g: Float = 200
  val maxSpeed: Float = 100
  val minSpeed: Float = 20

  override def mouseMove(x: Double, y: Double): Boolean = {
    mouse.set(x, y)
    true
  }

  override def setup(window: Window): Unit = {
    vertexArray = VertexArray()
    arrayBuffer = ArrayBuffer()
    elementsBuffer = ElementsBuffer()


    val width: Float = window.getWidth.toFloat
    val height: Float = window.getHeight.toFloat
    camera = Camera3.builder2d()
      .size(width, height)
      .position(width / 2, height / 2)
      .build()

    vertexArray.bind()
    arrayBuffer.bind()
    elementsBuffer.bind()


    arrayBuffer.setVertexAttributes(Array(
      VertexAttribute("position", 2),
      VertexAttribute("color", 4),
      VertexAttribute("alpha", 1),
    ))

    val colors = Array(
      Colors.green400,
      Colors.pink400,
      Colors.blue400,
      Colors.orange400,
      Colors.red400,
      Colors.purple400,
      Colors.violet400,
      Colors.amber400,
      Colors.sky400,
    )

    particles = (for i <- 0 until n yield
      val angle = random.nextFloat() * Math.PI * 2
      val speed = random.nextFloat() * 50 + 10
      Particle(
        position = Vector2f(mouse),
        acceleration = Vector2f(Math.cos(angle).toFloat, Math.sin(angle).toFloat).mul(speed),
        life = 0,
        lifeSpan = random.nextFloat() * 5,
        color = colors(random.nextInt(colors.length))
      )).toArray

    elementsBuffer.setData((for
      i <- particles.indices
    yield
      val index = i * 4
      Array[Int](
        index + 0, index + 1, index + 2, index + 1, index + 2, index + 3
      )).flatten.toArray
    )


    shader = Shader.builder()
      .vertex("particle/vertex.glsl")
      .fragment("particle/fragment.glsl")
      .build()

    println(shader.getLog)
    println(shader.isCompiled)
  }

  override def loop(delta: Float): Unit = {
    val startTime = System.nanoTime()

    val window = getWindow
    window.pollEvents()
    if (window.shouldClose()) close()
    Utils.clear(Colors.black, Buffer.COLOR_BUFFER)


    particles.foreach { p =>
      p.life += delta
      if (p.life > p.lifeSpan) p.restart(mouse, maxSpeed, minSpeed, random)
    }

    arrayBuffer.setData((for
      particle <- particles
      k <- 0 until 4
    yield
      val size = 10
      val p = particle.position
      val acc = particle.acceleration
      val t = particle.life
      val color = particle.color
      Array[Float](
        p.x + (k % 2) * size + acc.x * t,
        p.y + (k / 2) * size + acc.y * particle.life + g * t * t / 2,
        color.x,
        color.y,
        color.z,
        color.w,
        1f-particle.life/particle.lifeSpan
      )).flatten.toArray
    )

    //    Utils.enableLines(2)
    Utils.enableBlending()
    shader.use()
    shader.setUniform("view", camera.view)
    shader.setUniform("projection", camera.projection)
    vertexArray.bind()
    shader.drawElements(Primitive.TRIANGLES, particles.length * 2, ElementsDataType.UNSIGNED_INT, 3)
    Utils.disableBlending()
    //    shader.drawArrays(Primitive.TRIANGLES, 0, 100)
    //    Utils.disableLines()

    val endTime = System.nanoTime()
    val frameTime = endTime - startTime
    val nano = frameTime % 1000
    val micro = frameTime / 1000 % 1000
    val milli = frameTime / 1000000
    val fps = 1000 / (if milli == 0 then 1 else milli)
    System.out.printf("\r %3d %3d %3d fps: %3d", milli, micro, nano, fps)
  }
}
