package com.systemvi.examples.sdf

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.math.sdf.{Plane, Sphere, Union}
import com.systemvi.engine.ui.utils.data.Colors
import org.joml.Vector3f


object Map2 {
  val epsilon=0.001f
  val roughness=0.01f
  val red=Material(roughness,1f,Colors.red500)
  val blue=Material(roughness,1f,Colors.blue500)
  val floor=Material(roughness,1f,Colors.orange300)
  val sky=Material(1,1,Colors.blue100)

  def getMaterial(p: Vector3f): Material = {
    if (Sphere(p,new Vector3f(-100,100,0),100)<epsilon) return red
    if (Sphere(p,new Vector3f( 100,100,0),100)<epsilon) return blue
    if (Plane(p) < epsilon) return floor
    sky
  }

  def getDistance(p: Vector3f): Float = {
    Union(
      Sphere(p,new Vector3f(-100,100,0),100),
      Sphere(p,new Vector3f( 100,100,0),100),
      Plane(p)
    )
  }

  def renderer(camera:Camera3):RayMarchRenderer = {
    camera.rotation(0,0,0).position(0,100,500).update()
    new RayMarchRenderer(
      Map2.getDistance,
      Map2.getMaterial,
      camera,
      maxDistance = 100000,
      epsilon=epsilon
    )
  }
}
