package com.systemvi.sdf.cpu.maps

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.math.sdf.{Plane, Sphere, Union}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.sdf.cpu.{Material, RayMarchRenderer}
import org.joml.Vector3f

import scala.util.Random
import scala.util.control.Breaks.*
import scala.util.boundary.Break

object Map {
  val epsilon = 0.001f
  val random = new Random()

  val materials: Array[Material] = Array[Material](
    Material(0.3f, 0f, Colors.red500), //sphere red
    Material(0.3f, 0f, Colors.orange500),
    Material(0.3f, 0f, Colors.green500)
  )
  val floor = Material(0.1f, 0.5f, Colors.yellow500)
  val sky = Material(0, 0, Colors.blue200)

  val spawnRadius = 300
  val sphereCount = 150

  case class Ball(center: Vector3f, radius: Float, material: Material)

  var spheres: Array[Ball] = (0 until sphereCount).map { _ =>
    var x = 0f
    var y = 0f
    var z = 0f
    var r = 0f
    breakable {
      while (true) {
        x = random.nextInt(spawnRadius * 2).toFloat - spawnRadius
        z = random.nextInt(spawnRadius * 2).toFloat - spawnRadius
        val d = Math.sqrt(x * x + y * y).toFloat
        r = (spawnRadius - d) / spawnRadius * 10f + 3f
        y = r
        if (d < spawnRadius) break()
      }
    }
    Ball(new Vector3f(x, y, z), r, materials(random.nextInt(materials.length)))
  }.toArray

  def getMaterial(p: Vector3f): Material = {
    var m:Material=null
    breakable{
      spheres.foreach {
        case Ball(center, radius, material) => if (Sphere(p, center, radius) < epsilon) {
          m=material
          break
        }
      }
    }
    if(m!=null)return m    
    if (Plane(p) < epsilon) return floor
    sky
  }

  def getDistance(p: Vector3f): Float = {
    Union(
      spheres.map { s => Sphere(p, s.center, s.radius) } :+
        Plane(p) *
    )
  }

  def renderer(): RayMarchRenderer = {
    new RayMarchRenderer(
      Map.getDistance,
      Map.getMaterial,
      Camera3.builder3d().position(0, 100, -400).rotation(-0.3f, Math.PI.toFloat, 0).build(),
      epsilon = epsilon,
      maxDistance = 100000
    )
  }
}
