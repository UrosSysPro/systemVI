package com.systemvi.engine.camera

import com.systemvi.engine.window.{InputProcessor, Window}
import org.joml.{Vector2f, Vector3f}
import org.lwjgl.glfw.GLFW

class CameraController3(var camera:Camera3,
                        var sensistivity:Float,
                        var speed:Float,
                        var scaleX:Float,
                        var scaleY:Float,
                        var window:Window,
                        var fov:Float,
                        var aspect:Float,
                        var near:Float,
                        var far:Float
                       ) extends InputProcessor{
  var focused:Boolean=false

  var forward:Boolean=false
  var backward:Boolean=false
  var left:Boolean=false
  var right:Boolean=false
  var up:Boolean=false
  var down:Boolean=false

  var mouseCurrent=new Vector2f()
  var mousePrevious=new Vector2f()
  override def keyDown(key: Int, scancode: Int, mods: Int): Boolean = if (focused) {
    key match {
      case GLFW.GLFW_KEY_Q =>
        GLFW.glfwSetInputMode(window.getId,GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_NORMAL)
        focused=false
      case GLFW.GLFW_KEY_ESCAPE =>
        GLFW.glfwSetInputMode(window.getId,GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_NORMAL)
        focused=false
      case GLFW.GLFW_KEY_W=>forward=true
      case GLFW.GLFW_KEY_S=>backward=true
      case GLFW.GLFW_KEY_A=>left=true
      case GLFW.GLFW_KEY_D=>right=true
      case GLFW.GLFW_KEY_SPACE=>up=true
      case GLFW.GLFW_KEY_LEFT_SHIFT=>down=true
      case _ =>
    }
    true
  } else {
    false
  }
  override def keyUp(key: Int, scancode: Int, mods: Int): Boolean= if(focused){
    key match {
      case GLFW.GLFW_KEY_W=>forward=false
      case GLFW.GLFW_KEY_S=>backward=false
      case GLFW.GLFW_KEY_A=>left=false
      case GLFW.GLFW_KEY_D=>right=false
      case GLFW.GLFW_KEY_SPACE=>up=false
      case GLFW.GLFW_KEY_LEFT_SHIFT=>down=false
      case _ =>
    }
    true
  }else{
    false
  }
  override def mouseDown(button: Int, mods: Int, x: Double, y: Double): Boolean = if(focused){
    true
  }else{
    mouseCurrent.set(x.toFloat,y.toFloat)
    mousePrevious.set(x.toFloat,y.toFloat)
    GLFW.glfwSetInputMode(window.getId,GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_DISABLED)
    focused=true
    true
  }
  override def mouseUp(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    focused
  }
  override def mouseMove(x: Double, y: Double): Boolean = {
    mouseCurrent.set(x.toFloat,y.toFloat)
    focused
  }
  override def scroll(offsetX: Double, offsetY: Double): Boolean = if(focused){
//    val scale=if(offsetY>0)1.1f else 0.9f
//    camera.scale.mul(scale,scale,1)
    fov+=offsetY.toFloat*0.01f
    true
  }else{
    false
  }
  override def resize(width: Int, height: Int): Boolean = {
    aspect=width.toFloat/height.toFloat
    false
  }
  def update(delta:Float): Unit = {
    val dx:Float=mouseCurrent.x-mousePrevious.x
    val dy:Float=mouseCurrent.y-mousePrevious.y
    mousePrevious.set(mouseCurrent)

    val forwardDir=new Vector3f(
      -Math.sin(camera.rotation.y).toFloat*speed*delta,
      0,
      -Math.cos(camera.rotation.y).toFloat*speed*delta
    )
    val rightDir=new Vector3f(
      Math.sin(camera.rotation.y+Math.PI/2).toFloat*speed*delta,
      0,
      Math.cos(camera.rotation.y+Math.PI/2).toFloat*speed*delta
    )

    if(focused) {
      if (forward) camera.position.add(forwardDir)
      if (backward) camera.position.sub(forwardDir)
      if (right) camera.position.add(rightDir)
      if (left) camera.position.sub(rightDir)
      if (up) camera.position.y += delta * speed
      if (down) camera.position.y -= delta * speed

      camera.rotation.y += dx * sensistivity * scaleX
      camera.rotation.x += dy * sensistivity * scaleY
    }
    camera.perspective(near,far,aspect,fov)
    camera.update()
  }
}
object CameraController3{
  class Builder{
    var camera:Camera3 = null
    var window:Window = null
    var sensistivity:Float = 0.001f
    var speed:Float = 1
    var scaleX:Float = -1
    var scaleY:Float = -1
    var near:Float=0.1f
    var far:Float=100f
    var aspect:Float=1f
    var fov:Float=Math.PI.toFloat/3f
    def camera(camera:Camera3):Builder={
      this.camera=camera
      this
    }
    def sensitivity(sensitivity:Float):Builder={
      this.sensistivity=sensistivity
      this
    }
    def speed(speed:Float):Builder={
      this.speed=speed
      this
    }
    def scaleX(scaleX:Float):Builder={
      this.scaleX=scaleX
      this
    }
    def scaleY(scaleY:Float):Builder={
      this.scaleY=scaleY
      this
    }

    def aspect(aspect:Float):Builder={
      this.aspect=aspect
      this
    }
    def fov(fov:Float):Builder={
      this.fov=fov
      this
    }
    def near(near:Float):Builder={
      this.near=near
      this
    }
    def far(far:Float):Builder={
      this.far=far
      this
    }

    def window(window: Window): Builder = {
      this.window = window
      this
    }
    def build():CameraController3={
      if(camera==null)camera=Camera3.builder3d().build()
      new CameraController3(camera, sensistivity, speed, scaleX, scaleY,window,fov, aspect, near, far)
    }
  }
  def builder():Builder=new Builder()
}