package com.systemvi.engine.camera

import org.joml.{Matrix4f, Vector2f, Vector3f, Vector4f}

class Camera3(val position:Vector3f,val rotation:Vector3f,val scale:Vector3f) {
  val view:Matrix4f=new Matrix4f()
  val projection:Matrix4f=new Matrix4f()
  val combined:Matrix4f=new Matrix4f()
  def position(x:Float,y:Float,z:Float):Camera3={
    position.set(x,y,z)
    this
  }
  def rotation(x:Float,y:Float,z:Float):Camera3={
    rotation.set(x,y,z)
    this
  }
  def scale(x:Float,y:Float,z:Float):Camera3={
    scale.set(x,y,z)
    this
  }
  def position(v:Vector3f):Camera3={
    position.set(v)
    this
  }
  def rotation(v:Vector3f):Camera3={
    rotation.set(v)
    this
  }
  def scale(v:Vector3f):Camera3={
    scale.set(v)
    this
  }
  def perspective(near:Float,far:Float,aspect:Float,fov:Float):Camera3={
    projection.identity().perspective(fov,aspect,near,far)
    this
  }
  def orthographic(left:Float,right:Float,bottom:Float,top:Float,near:Float,far:Float): Camera3 = {
    projection.identity().ortho(left,right,bottom,top,near,far)
    this
  }
  def update(): Camera3 = {
    view.identity()
      .scale(1f/scale.x,1f/scale.y,1f/scale.z)
      .rotateXYZ(-rotation.x,-rotation.y,-rotation.z)
      .translate(-position.x,-position.y,-position.z)
    combined.identity().mul(projection).mul(view)
    this
  }
}

object Camera3{
  class Builder2D{
    private val position:Vector2f=new Vector2f(0)
    private var rotation:Float=0
    private val scale:Vector2f=new Vector2f(1,-1)
    private val size:Vector2f=new Vector2f(0,0)
    def position(x:Float,y:Float):Builder2D={
      position.set(x,y)
      this
    }
    def rotation(r:Float):Builder2D={
      rotation=r
      this
    }
    def scale(x:Float,y:Float):Builder2D={
      scale.set(x,y)
      this
    }
    def size(width:Float,height:Float):Builder2D={
      size.set(width,height)
      this
    }
    def build():Camera3=
      new Camera3(
        new Vector3f(position.x,position.y,0),
        new Vector3f(0,0,rotation),
        new Vector3f(scale.x,scale.y,1)
      )
        .orthographic(-size.x/2,size.x/2,-size.y/2,size.y/2,0,1)
        .update()
  }
  class Builder3D{
    val camera=new Camera3(new Vector3f(0),new Vector3f(0),new Vector3f(1))
    var near:Float=0.1f
    var far:Float=100f
    var fov:Float=(Math.PI/3f).toFloat
    var aspect:Float=800f/600f
    def position(x:Float,y:Float,z:Float):Builder3D={
      camera.position(x,y,z)
      this
    }
    def rotation(x:Float,y:Float,z:Float):Builder3D={
      camera.rotation(x,y,z)
      this
    }
    def scale(x:Float,y:Float,z:Float):Builder3D={
      camera.scale(x,y,z)
      this
    }
    def fov(fov:Float):Builder3D={
      this.fov=fov
      this
    }
    def far(far:Float):Builder3D={
      this.far=far
      this
    }
    def near(near:Float):Builder3D={
      this.near=near
      this
    }
    def aspect(aspect:Float):Builder3D={
      this.aspect=aspect
      this
    }
    def build():Camera3=camera
      .perspective(near,far,aspect,fov)
      .update()
  }

  def builder2d(): Builder2D = new Builder2D()

  def builder3d(): Builder3D = new Builder3D()
}