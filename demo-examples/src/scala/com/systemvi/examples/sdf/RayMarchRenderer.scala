package com.systemvi.examples.sdf

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.ui.utils.data.Colors
import org.joml.{Matrix4f, Random, Vector3f, Vector4f}

import scala.util.control.Breaks._

case class Material(roughness:Float=0.3f, metallic:Float=1.0f, color:Vector4f = Colors.blue500)

class RayMarchRenderer(
                        val distance:Vector3f=>Float,
                        val material:Vector3f=>Material,
                        val camera:Camera3,
                        val seed:Int=System.currentTimeMillis().toInt,
                        val epsilon:Float=0.001f,
                        val maxDistance:Float=1000
                      ){
  private val random=new Random(seed)

  def RayMarch(ro: Vector3f, rd: Vector3f, iterations: Int): Vector3f = {
    var d:Float = 0
    val p=new Vector3f()
    breakable{
      for (_ <- 0 until iterations) {
        p.set(
          ro.x+rd.x*d,
          ro.y+rd.y*d,
          ro.z+rd.z*d
        )
        val min = distance(p)
        d += min
        if (d > maxDistance || min < epsilon) break()
      }
    }
    p.set(ro.x + rd.x * d, ro.y + rd.y * d, ro.z + rd.z * d)
  }

  def getNormal(p: Vector3f): Vector3f = new Vector3f(
    Map.getDistance(new Vector3f(p.x + 0.01f, p.y, p.z)) - Map.getDistance(new Vector3f(p.x - 0.01f, p.y, p.z)),
    Map.getDistance(new Vector3f(p.x, p.y + 0.01f, p.z)) - Map.getDistance(new Vector3f(p.x, p.y - 0.01f, p.z)),
    Map.getDistance(new Vector3f(p.x, p.y, p.z + 0.01f)) - Map.getDistance(new Vector3f(p.x, p.y, p.z - 0.01f))
  ).normalize

//  def SimulatePhoton(x: Float, y: Float, bounces: Int, r: Random, iterations: Int): Vector4f = {
//    val color = new Vector4f(1)
//    val ro = new Array[Vector3f](bounces + 1)
//    val rd = new Array[Vector3f](bounces + 1)
//    val inverted = new Matrix4f(camera.view).invert
//    val focus = new Vector4f(0, 0, 2.2f, 1).mul(inverted)
//    val point = new Vector4f(x, y, 0, 1).mul(inverted)
//    ro(0) = new Vector3f(focus.x, focus.y, focus.z)
//    rd(0) = new Vector3f(point.x, point.y, point.z).sub(ro(0)).normalize
//    breakable {
//      for (k <- 0 until bounces) {
//        val p = RayMarch(ro(k), rd(k), iterations)
//        val normal = getNormal(p)
//        val m = material(p)
//        val c = m.color
//        if (p.distance(ro(k)) > 1000) break //todo: break is not supported
//        ro(k + 1) = new Vector3f(p).add(normal.x * 2 * epsilon, normal.y * 2 * epsilon, normal.z * 2 * epsilon)
//        rd(k + 1) = new Vector3f(rd(k)).reflect(normal).add(new Vector3f(r.nextFloat * 2 - 1, r.nextFloat * 2 - 1, r.nextFloat * 2 - 1).mul(m.roughness)).normalize
//        if (r.nextFloat < m.metallic) color.mul(c)
//      }
//    }
//    color
//  }
  def SimulatePhoton(x: Float, y: Float, bounces: Int, r: Random, iterations: Int): Vector4f = {
    val color = new Vector4f(1)
    val rayOrigin=new Vector3f()
    val rayDirection=new Vector3f()
    val inverted = new Matrix4f(camera.view).invert
    val focus = new Vector4f(0, 0, 2.2f, 1).mul(inverted)
    val point = new Vector4f(x, y, 0, 1).mul(inverted)
    rayOrigin.set(focus.x,focus.y,focus.z)
    rayDirection.set(point.x,point.y,point.z).sub(rayOrigin).normalize()

    breakable {
      for (_ <- 0 until bounces) {
        val p = RayMarch(rayOrigin, rayDirection, iterations)
        val traveled=p.distance(rayOrigin)
        val normal = getNormal(p)
        val m = material(p)
        val c = m.color
        rayOrigin.set(p).add(normal.x*2*epsilon,normal.y*2*epsilon,normal.z*2*epsilon)
        rayDirection.reflect(normal).add(
          (r.nextFloat()*2-1)*m.roughness,
          (r.nextFloat()*2-1)*m.roughness,
          (r.nextFloat()*2-1)*m.roughness
        ).normalize()
        color.mul(c)
        if (p.distance(point.x,point.y,point.z) > maxDistance||traveled>maxDistance) break()
      }
    }
    color
  }

  def calculatePixel(i: Int, j: Int, width:Int, height:Int, bounces: Int, samples: Int, iterations: Int): Vector4f = {
    var x = .0f
    var y = .0f
    x = i
    x /= width
    x = x * 2 - 1
    x *= width.toFloat / height.toFloat
    y = j
    y /= height
    y = 2 * y - 1
    y *= -1
    val color = new Vector4f(0)
    for (k <- 0 until samples) {
      color.add(SimulatePhoton(x, y, bounces, random, iterations))
    }
    color.div(samples)
    color
  }

}