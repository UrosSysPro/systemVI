package com.systemvi.engine.noise

import org.joml.{Vector2f, Vector2i, Vector3f, Vector3i}

import scala.util.Random

class Perlin2d(gridSize: Vector2i = new Vector2i(10, 10), seed: Int = System.nanoTime().toInt) extends Noise[Vector2f] {
  private val random: Random = new Random(seed)
  private val grid: Seq[Seq[Vector2f]] =
    for (i <- 0 until gridSize.x) yield
      for (j <- 0 until gridSize.y) yield
        val a = random.nextFloat() * Math.PI * 2
        //        val a=random.nextInt(4)/4f*Math.PI*2
        new Vector2f(Math.cos(a).toFloat, Math.sin(a).toFloat).normalize()
  //  6 t5 -15 t4 +10 t3

  private def smoothstep(value: Float): Float = (6 * value * value - 15 * value + 10) * value * value * value

  //  private def smoothstep(value:Float):Float=3*value*value-2*value*value*value
  //  private def smoothstep(value:Float):Float=Math.sin(value*Math.PI/2).toFloat
  private def interpolate(a0: Float, a1: Float, w: Float): Float = (a1 - a0) * w + a0

  private def randomGradient(ix: Int, iy: Int): Vector2f = grid(ix)(iy)

  private def dotGridGradient(ix: Int, iy: Int, x: Float, y: Float): Float = {
    val gradient = randomGradient(ix, iy)

    val dx = x - ix.toFloat
    val dy = y - iy.toFloat

    dx * gradient.x + dy * gradient.y
  }

  override def get(point: Vector2f): Float = {
    var x = point.x % (gridSize.x - 1)
    if (x < 0) x = gridSize.x.toFloat - 1 + x
    var y = point.y % (gridSize.y - 1)
    if (y < 0) y = gridSize.y.toFloat - 1 + y

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

    val value = interpolate(ix0, ix1, sy) * 0.5f + 0.5f
    value
  }

  def apply(gridSize: Vector2i, seed: Int): Perlin2d = new Perlin2d(gridSize, seed)
}

class Perlin3(gridSize: Vector3i = new Vector3i(10, 10, 10), seed: Int = System.nanoTime().toInt) extends Noise[Vector3f] {
  private val random: Random = new Random(seed)
  private val grid: Seq[Seq[Seq[Vector3f]]] =
    for (i <- 0 until gridSize.x) yield
      for (j <- 0 until gridSize.y) yield
        for (k <- 0 until gridSize.z) yield
          new Vector3f(random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1).normalize()

  private def smoothstep(value: Float): Float = (6 * value * value - 15 * value + 10) * value * value * value

  private def interpolate(a0: Float, a1: Float, w: Float): Float = (a1 - a0) * w + a0

  private def randomGradient(ix: Int, iy: Int, iz: Int): Vector3f = grid(ix)(iy)(iz)

  private def dotGridGradient(ix: Int, iy: Int, iz: Int, x: Float, y: Float, z: Float): Float = {
    val gradient = randomGradient(ix, iy, iz)

    val dx = x - ix.toFloat
    val dy = y - iy.toFloat
    val dz = y - iz.toFloat

    dx * gradient.x + dy * gradient.y
  }

  override def get(point: Vector3f): Float = {
    var x = point.x % (gridSize.x - 1)
    if (x < 0) x = gridSize.x.toFloat - 1 + x
    var y = point.y % (gridSize.y - 1)
    if (y < 0) y = gridSize.y.toFloat - 1 + y
    var z = point.z % (gridSize.z - 1)
    if (z < 0) z = gridSize.z.toFloat - 1 + z

    val x0 = x.floor.toInt
    val x1 = x0 + 1
    val y0 = y.floor.toInt
    val y1 = y0 + 1
    val z0 = z.floor.toInt
    val z1 = z0 + 1

    val sx = smoothstep(x - x0.toFloat)
    val sy = smoothstep(y - y0.toFloat)
    val sz = smoothstep(z - z0.toFloat)

    var n0 = dotGridGradient(x0, y0, z0, x, y, z)
    var n1 = dotGridGradient(x1, y0, z0, x, y, z)
    val ix0 = interpolate(n0, n1, sx)

    n0 = dotGridGradient(x0, y1, z0, x, y, z)
    n1 = dotGridGradient(x1, y1, z0, x, y, z)
    val ix1 = interpolate(n0, n1, sx)

    val value = interpolate(ix0, ix1, sy) * 0.5f + 0.5f
    value
  }

  def apply(gridSize: Vector2i, seed: Int): Perlin2d = new Perlin2d(gridSize, seed)
}