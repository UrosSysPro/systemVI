package com.systemvi.engine.noise

import org.joml.{Vector2f, Vector2i}

import scala.util.Random

trait Perlin[A]{
  def get(point:A):Float
}

class Perlin2(gridSize:Vector2i=new Vector2i(10,10),seed:Int=System.nanoTime().toInt)extends Perlin[Vector2f]{
  private val random:Random=new Random(seed)
  private val grid:Seq[Seq[Vector2f]] =
    for(i<-0 until gridSize.x)yield
      for(j<-0 until gridSize.y)yield
        val a=random.nextFloat()*Math.PI*2
//        val a=random.nextInt(4)/4f*Math.PI*2
        new Vector2f(Math.cos(a).toFloat,Math.sin(a).toFloat).normalize()
//  6 t5 -15 t4 +10 t3

  private def smoothstep(value:Float):Float=(6*value*value-15*value+10)*value*value*value
//  private def smoothstep(value:Float):Float=3*value*value-2*value*value*value
//  private def smoothstep(value:Float):Float=Math.sin(value*Math.PI/2).toFloat
  private def interpolate(a0: Float, a1: Float, w: Float):Float= (a1 - a0) * w + a0

  private def randomGradient (ix:Int,iy:Int):Vector2f = grid(ix)(iy)

  private def dotGridGradient (ix:Int, iy:Int, x:Float, y:Float):Float = {
    val gradient = randomGradient(ix, iy)

    val dx = x - ix.toFloat
    val dy = y - iy.toFloat

    dx * gradient.x + dy * gradient.y
  }

  override def get(point: Vector2f): Float = {
    val x=point.x
    val y=point.y

    val x0 = x.floor.toInt
    val x1 = x0 + 1
    val y0 = y.floor.toInt
    val y1 = y0 + 1

    val sx = smoothstep(x - x0.toFloat)
    val sy = smoothstep(y - y0.toFloat)

    var n0 = dotGridGradient(x0, y0, x, y)
    var n1 = dotGridGradient(x1, y0, x, y)
    val ix0 = interpolate(n0, n1, sx)

    n0 = dotGridGradient(x0, y1, x, y)
    n1 = dotGridGradient(x1, y1, x, y)
    val ix1 = interpolate(n0, n1, sx)

    val value = interpolate(ix0, ix1, sy)*0.5f+0.5f
    value
  }

  def apply(gridSize: Vector2i, seed: Int): Perlin2 = new Perlin2(gridSize, seed)
}