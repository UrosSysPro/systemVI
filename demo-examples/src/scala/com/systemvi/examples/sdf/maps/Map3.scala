package com.systemvi.examples.sdf.maps

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.math.sdf.{Box, Plane, Union}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.examples.sdf.{Material, RayMarchRenderer}
import org.joml.{Vector3f, Vector4f}

object Map3 {

  val epsilon=0.001f
  val left=Material(0.1f,0f,Colors.red500)
  val right=Material(0.1f,0f,Colors.blue500)
  val back=Material(0.1f,0f,Colors.green500)
  val top=Material(0.1f,0f,Colors.yellow500)
  val floor=Material(0.1f,0f,Colors.yellow500)
  val light=Material(0,1,new Vector4f(1))
  val side=200
  val width=4

  def distance(p:Vector3f): Float= {
    Union(
      Box(p,new Vector3f(width,side,side),translate = new Vector3f(-side,side,0)), // left
      Box(p,new Vector3f(width,side,side),translate = new Vector3f( side,side,0)), // right
      Box(p,new Vector3f(side,side,width),translate = new Vector3f(  0,side,-side)), // back
      Box(p,new Vector3f(side,width,side),translate = new Vector3f(  0,2*side,0)), // top
      Box(p,new Vector3f(20,5,20),translate = new Vector3f(  0,2*side-2*width,0)), // top
      Plane(p)
    )
  }

  def material(p:Vector3f):Material={
    if(Box(p,new Vector3f(width,side,side),translate = new Vector3f(-side,side,0))<epsilon)return left
    if(Box(p,new Vector3f(width,side,side),translate = new Vector3f( side,side,0))<epsilon)return right
    if(Box(p,new Vector3f(side,side,width),translate = new Vector3f(  0,side,-side))<epsilon)return back
    if(Box(p,new Vector3f(side,width,side),translate = new Vector3f(  0,2*side,0))<epsilon)return top
    if(Box(p,new Vector3f(20,5,20),translate = new Vector3f(  0,2*side-2*width,0))<epsilon)return light
    if(Plane(p)<epsilon)return floor
    Material(0,0,Colors.blue200)
  }


  def renderer():RayMarchRenderer={
    new RayMarchRenderer(
      distance,
      material,
      Camera3.builder3d()
        .position(0,200,700)
        .build()
    )
  }
}
