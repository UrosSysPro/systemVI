package com.systemvi.engine.noise

import org.joml.{Vector2f, Vector2i}

import scala.util.Random


class Voronoi2(gridSize: Vector2i = new Vector2i(10, 10), seed: Int = System.nanoTime().toInt) extends Noise[Vector2f]:

  private val random: Random = new Random(seed)

  private val grid: Seq[Seq[Vector2f]] = for (i <- 0 until gridSize.x) yield for (j <- 0 until gridSize.y) yield
    val x = i.toFloat
    val y = j.toFloat
    new Vector2f(x + random.nextFloat(), y + random.nextFloat())

  override def get(point: Vector2f): Float =
    val gridX = point.x.floor.toInt
    val gridY = point.y.floor.toInt
    var min = 1f

    for (i <- gridX - 1 to gridX + 1; j <- gridY - 1 to gridY + 1)
      var x = i % gridSize.x
      if (x < 0) x = gridSize.x + x
      var y = j % gridSize.y
      if (y < 0) y = gridSize.y + y
      val gridPoint = grid(x)(y)
      val d = gridPoint.distance(point)
      if (d < min) min = d
    min

