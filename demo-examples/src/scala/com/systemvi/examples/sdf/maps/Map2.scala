package com.systemvi.examples.sdf.maps

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.math.sdf.{Plane, Sphere, Union}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.examples.sdf.{Material, RayMarchRenderer}
import org.joml.Vector3f


object Map2 {
  val epsilon=0.001f
  val red=Material(0.2f,1,Colors.red500)
  val blue=Material(1,0,Colors.blue500)
  val floor=Material(0.1f,0.5f,Colors.orange300)
  val sky=Material(1,1,Colors.blue100)

  def getMaterial(p: Vector3f): Material = {
    if (Sphere(p,new Vector3f(-100,100,0),100)<epsilon) return red
    if (Sphere(p,new Vector3f( 100,100,0),100)<epsilon) return blue
    if (Plane(p) < epsilon) return floor
//    if (-Plane(p,new Vector3f(0,200,0)) < epsilon) return floor
    sky
  }

  def getDistance(p: Vector3f): Float = {
    Union(
      Sphere(p,new Vector3f(-100,100,0),100),
      Sphere(p,new Vector3f( 100,100,0),100),
      Plane(p)
//      -Plane(p,new Vector3f(0,200,0))
    )
  }

  def renderer():RayMarchRenderer = {
    new RayMarchRenderer(
      Map2.getDistance,
      Map2.getMaterial,
      Camera3.builder3d().rotation(0,0,0).position(0,100,500).build(),
      maxDistance = 10000,
      epsilon=epsilon
    )
  }
}
