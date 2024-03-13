package com.systemvi.engine.camera

import org.joml.{Matrix4f, Vector3f, Vector4f}

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
  def update(): Unit = {
    view.identity().scale(scale).rotateXYZ(rotation).translate(position)
    combined.identity().mul(projection).mul(view)
  }
}

object Camera3{
  class Builder2D{
    val position:Vector3f=new Vector3f(0)
    val rotation:Vector3f=new Vector3f(0)
    val scale:Vector3f=new Vector3f(0)
    def build():Camera3=new Camera3(position,rotation, scale)
  }
  class Builder3D{
    val position:Vector3f=new Vector3f(0)
    val rotation:Vector3f=new Vector3f(0)
    val scale:Vector3f=new Vector3f(0)
    def build():Camera3=new Camera3(position,rotation, scale)
  }
  def builder2d(): Unit = new Builder2D()
  def builder3d(): Unit = new Builder3D()
}