package com.systemvi.physics2d;

import com.systemvi.engine.application.{Application, Game}
import com.systemvi.engine.camera.{Camera, Camera3}
import com.systemvi.engine.physics2d.PhysicsBody
import com.systemvi.engine.renderers.ShapeRenderer
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*
import org.joml.{Vector2f, Vector4f}

import java.util.ArrayList
import org.lwjgl.opengl.{GL11, GL33}

case class PhysicsBody(body:Body,size:Vector2f)

object Physics extends Game(3, 3, 60, 800, 600, "Physics") {

  var world: World = null
  var renderer: ShapeRenderer = null
  var camera: Camera3 = null
  var mouse = Vector2f()
  var bodies: Array[PhysicsBody] = Array()
  val size: Float = 20

  override def setup(window: Window): Unit = {
    val width = window.getWidth.toFloat
    val height = window.getHeight.toFloat
    world = World(new Vec2(0, 1000))
    renderer = ShapeRenderer()
    camera = Camera3.builder2d()
      .position(width / 2, height / 2)
      .size(width, height)
      .build()

    camera.update()
    renderer.setView(camera.view)
    renderer.setProjection(camera.projection)

    createWalls()
  }

  override def loop(delta: Float): Unit = {
    val window = getWindow
    if (window.shouldClose()) close()
    Utils.clear(Colors.black, Buffer.COLOR_BUFFER)
    //input
    window.pollEvents()
    //update
    world.step(delta, 10, 10)
    //draw
    for (element <- bodies) {
      val x = element.body.getPosition.x
      val y = element.body.getPosition.y
      val a = element.body.getAngle
      renderer.rect(
          x - element.size.x/2f,
          y - element.size.y/2f,
          element.size.x,
          element.size.y,
          Colors.red400, a
      )
    }
    renderer.flush()
  }

  def createWalls(): Unit = {
    addBody(0,300,20,600,BodyType.STATIC)
    addBody(780,300,20,600,BodyType.STATIC)
    addBody(400,0,800,20,BodyType.STATIC)
    addBody(400,580,800,20,BodyType.STATIC)
  }

  override def mouseDown(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    addBody(x.toFloat,y.toFloat,20,20)
    true
  }

  def addBody(x:Float,y:Float,width:Float,height:Float,`type`:BodyType=BodyType.DYNAMIC):Unit = {
      val bodyDef = BodyDef()
      bodyDef.position.set(x, y)
      bodyDef.`type` = `type`
      bodyDef.linearDamping = 0
      bodyDef.angularDamping = 0

      val fixtureDef = FixtureDef()
      fixtureDef.density = 1
      fixtureDef.restitution = 0.5f
      fixtureDef.friction = 0.7f
      val shape = PolygonShape()
      shape.setAsBox(width/2, height/2)

      fixtureDef.shape = shape

      val body = world.createBody(bodyDef)
      body.createFixture(fixtureDef)
      bodies=bodies :+ PhysicsBody(body, Vector2f(width,height))
  }

  override def resize(width: Int, height: Int): Boolean = {
    camera.position.set(width / 2f, height / 2f, 0)
    camera.orthographic(-width / 2f, width / 2f, -height / 2f, height / 2f, 0, 1)
    camera.update()
    true
  }

  override def mouseMove(x: Double, y: Double): Boolean = {
    mouse.set(x, y)
    true
  }
}
