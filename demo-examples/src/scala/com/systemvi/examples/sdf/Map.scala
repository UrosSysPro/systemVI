package com.systemvi.examples.sdf

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.math.sdf.{Plane, Sphere, Union}
import com.systemvi.engine.ui.utils.data.Colors
import org.joml.Vector3f

import scala.util.Random

object Map {
  val epsilon=0.001f
  val random = new Random()

  val materials: Array[Material] = Array[Material](
    Material(0.3f, 1f, Colors.red500), //sphere red
    Material(0.3f, 1f, Colors.green500), //sphere green
    Material(0.3f, 1f, Colors.orange500),
    Material(0.3f, 1f, Colors.blue500),
//    Material(0.3f, 1f, Colors.purple500),
    Material(0.3f, 1f, Colors.yellow500),
    Material(0.3f, 1f, Colors.fuchsia500)
  )
  val floor=Material(0.1f,1f,Colors.orange300)
  val sky=Material(0,1,Colors.blue100)

  val spawnRadius=100
  val sphereCount=5

  case class Ball(center:Vector3f,radius:Float,material: Material)

  var spheres: Array[Ball] = (0 until sphereCount).map{_=>
    var insideCircle=false
    var x=0f
    var y=0f
    var z=0f
    var r=0f
    while(!insideCircle) {
      x = random.nextInt(spawnRadius*2)-spawnRadius
      z = random.nextInt(spawnRadius*2)-spawnRadius
      val d=Math.sqrt(x*x+y*y).toFloat
      r = (spawnRadius-d)/spawnRadius*10f+3f
      y = r
      if(d<spawnRadius)insideCircle=true
    }
    Ball(new Vector3f(x,y,z),r,materials(random.nextInt(materials.length)))
  }.toArray

  def getMaterial(p: Vector3f): Material = {
    spheres.foreach{
      case Ball(center, radius, material) => if(Sphere(p,center,radius)<epsilon) return material
    }
    if (Plane(p) < epsilon) return floor
    sky
  }

  def getDistance(p: Vector3f): Float = {
    Union(
      spheres.map { s => Sphere(p, s.center, s.radius)}:_*,
      Plane(p, new Vector3f(0, 0, 0))
    )
  }

  def renderer(camera:Camera3):RayMarchRenderer = {
    new RayMarchRenderer(
      Map.getDistance,
      Map.getMaterial,
      camera,
      epsilon = epsilon
    )
  }
}
