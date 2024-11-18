package com.systemvi.snake

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.texture.Texture.Repeat
import com.systemvi.engine.texture.{Texture, TextureRegion}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.window.InputAdapter
import org.joml.Vector2i
import org.lwjgl.glfw.GLFW

import scala.util.Random

class Food {
  val random=Random()
  val position:Vector2i=Vector2i()
  val color=Colors.red300
  def randomize(width:Int,height:Int): Unit = {
    position.set(random.nextInt(width),random.nextInt(height))
    println(s"${position.x} ${position.y}")
  }
  def draw(renderer:ShapeRenderer,region:TextureRegion): Unit = {
    renderer.draw(Square(
      position.x.toFloat*SnakePart.size,
      position.y.toFloat*SnakePart.size,
      SnakePart.size.toFloat,
      color,
      region
    ))
  }
}

class Game extends InputAdapter{
  val width:Int=800/SnakePart.size
  val height:Int=600/SnakePart.size
  val player:Player=Player()
  val food:Food=Food()
  food.randomize(width, height)
  var shapeRenderer: ShapeRenderer = null
  var texture: Texture = null
  var camera: Camera3= null
  var textureRegion: Array[Array[TextureRegion]] = null
  var emptyRegion: TextureRegion = null

  val screenWidth = 800f
  val screenHeight = 600f
  camera = Camera3.builder2d()
    .position(screenWidth / 2, screenHeight / 2)
    .size(screenWidth,screenHeight)
    .build()
  shapeRenderer = ShapeRenderer()
  texture = Texture.builder()
    .file("assets/tiles.png")
    .borderColor(Colors.white)
    .verticalRepeat(Repeat.CLAMP_BORDER)
    .horizontalRepeat(Repeat.CLAMP_BORDER)
    .build()
  textureRegion = TextureRegion.split(texture, 18, 18)
  emptyRegion = TextureRegion(texture, -1, -1, 0, 0)

  shapeRenderer.texture = texture
  shapeRenderer.view(camera.view)
  shapeRenderer.projection(camera.projection)

  var counter:Float=0
  var updatesPerSecond=15

  def update(delta:Float): Unit = {
    counter+=delta
    if(counter>1f/updatesPerSecond){
      counter=0
      player.move()
    }
  }

  def draw(): Unit = {
    val a=counter/(1f/updatesPerSecond)
    player.draw(shapeRenderer,emptyRegion,a)
    food.draw(shapeRenderer,emptyRegion)
    if(
      player.parts.head.position.x==food.position.x &&
      player.parts.head.position.y==food.position.y
    ){
      player.add()
      food.randomize(width, height)
    }
    shapeRenderer.flush()
  }

  override def keyDown(key: Int, scancode: Int, mods: Int): Boolean = {
    if(key==GLFW.GLFW_KEY_W) {
      player.direction.set(0, -1)
      return true
    }
    if (key == GLFW.GLFW_KEY_S) {
      player.direction.set(0, 1)
      return true
    }
    if (key == GLFW.GLFW_KEY_A) {
      player.direction.set(-1, 0)
      return true
    }
    if (key == GLFW.GLFW_KEY_D) {
      player.direction.set(1, 0)
      return true
    }
    if (key == GLFW.GLFW_KEY_SPACE) {
      player.add()
      return true
    }
    false
  }
}
