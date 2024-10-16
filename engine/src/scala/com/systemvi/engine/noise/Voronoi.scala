package com.systemvi.engine.noise

import org.joml.{Vector2f, Vector2i}

import scala.util.Random


class Voronoi2(gridSize: Vector2i = new Vector2i(10, 10), seed: Int = System.nanoTime().toInt) extends Noise[Vector2f]:

  private val random: Random = Random(seed)

  private val v = Vector2f()

  private val grid: Seq[Seq[Vector2f]] = for (i <- 0 until gridSize.x) yield for (j <- 0 until gridSize.y) yield
    new Vector2f(random.nextFloat(), random.nextFloat())

  override def get(point: Vector2f): Float =
    val gridX = point.x.floor.toInt
    val gridY = point.y.floor.toInt
    var min = 1f

    for (i <- -1 to  1; j <- -1 to 1)
      var x = (gridX + i) % gridSize.x
      if (x < 0) x = gridSize.x + x
      var y = (gridY + j) % gridSize.y
      if (y < 0) y = gridSize.y + y
      val gridPoint = grid(x)(y)
      v.set(gridPoint).add((gridX + i).toFloat,(gridY + j).toFloat)
      val d = v.distance(point)
      if (d < min) min = d
    min

