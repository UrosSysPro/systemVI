package com.systemvi.examples.sdf

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.examples.sdf.RayMarchRenderer.epsilon
import org.joml.{Matrix4f, Random, Vector3f, Vector4f}

import scala.util.control.Breaks._

case class Material(roughness:Float=0.3f, metallic:Float=1.0f, color:Vector4f = Colors.blue500)

class RayMarchRenderer(val distance:Vector3f=>Float,val material:Vector3f=>Material,val camera:Camera3){

  val random=new Random()

  def RayMarch(ro: Vector3f, rd: Vector3f, iterations: Int): Vector3f = {
    var d = 0
    breakable{
      for (k <- 0 until iterations) {
        val p = new Vector3f(ro.x + rd.x * d, ro.y + rd.y * d, ro.z + rd.z * d)
        d += distance(p)
        if (d > 1000) break()
        if (distance(p) < epsilon) break()
      }
    }
    new Vector3f(ro.x + rd.x * d, ro.y + rd.y * d, ro.z + rd.z * d)
  }

  def getNormal(p: Vector3f): Vector3f = new Vector3f(
    Map.getDistance(new Vector3f(p.x + 0.01f, p.y, p.z)) - Map.getDistance(new Vector3f(p.x - 0.01f, p.y, p.z)),
    Map.getDistance(new Vector3f(p.x, p.y + 0.01f, p.z)) - Map.getDistance(new Vector3f(p.x, p.y - 0.01f, p.z)),
    Map.getDistance(new Vector3f(p.x, p.y, p.z + 0.01f)) - Map.getDistance(new Vector3f(p.x, p.y, p.z - 0.01f))
  ).normalize

  def SimulatePhoton(x: Float, y: Float, bounces: Int, r: Random, iterations: Int): Vector4f = {
    val color = new Vector4f(1)
    val ro = new Array[Vector3f](bounces + 1)
    val rd = new Array[Vector3f](bounces + 1)
    val inverted = new Matrix4f(camera.view).invert
    val focus = new Vector4f(0, 0, 2.2f, 1).mul(inverted)
    val point = new Vector4f(x, y, 0, 1).mul(inverted)
    ro(0) = new Vector3f(focus.x, focus.y, focus.z)
    rd(0) = new Vector3f(point.x, point.y, point.z).sub(ro(0)).normalize
    breakable {
      for (k <- 0 until bounces) {
        val p = RayMarch(ro(k), rd(k), iterations)
        val normal = getNormal(p)
        val m = material(p)
        val c = m.color
        if (p.distance(ro(k)) > 1000) break //todo: break is not supported
        ro(k + 1) = new Vector3f(p).add(normal.x * 2 * epsilon, normal.y * 2 * epsilon, normal.z * 2 * epsilon)
        rd(k + 1) = new Vector3f(rd(k)).reflect(normal).add(new Vector3f(r.nextFloat * 2 - 1, r.nextFloat * 2 - 1, r.nextFloat * 2 - 1).mul(m.roughness)).normalize
        if (r.nextFloat < m.metallic) color.mul(c)
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

object RayMarchRenderer{
  val epsilon:Float=0.001f
}
