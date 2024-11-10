package com.systemvi.engine.noise

import org.joml.{Vector2f, Vector2i}

class FractalNoise2d(val octaves: List[Octave2]) extends Noise[Vector2f] {
  override def get(point: Vector2f): Float = octaves.foldLeft(0f) {
    case (acc, octave) =>
      acc + octave.get(point)
  }
}

object FractalNoise2d {
  def harmonicsPerlin(n: Int): FractalNoise2d = {
    FractalNoise2d(
      octaves = (0 until n).map { index =>
        Octave2(
          noise = Perlin2d(gridSize = Vector2i(100, 100)),
          frequency = Math.pow(2, index).toFloat,
          amplitude = Math.pow(0.5f, index + 1).toFloat,
        )
      }.toList
    )
  }

  def harmonicsVoronoi(n: Int): FractalNoise2d = {
    FractalNoise2d(
      octaves = (0 until n).map { index =>
        Octave2(
          noise = Voronoi2(gridSize = Vector2i(100, 100)),
          frequency = Math.pow(2, index).toFloat,
          amplitude = Math.pow(0.5f, index + 1).toFloat,
        )
      }.toList
    )
  }
}