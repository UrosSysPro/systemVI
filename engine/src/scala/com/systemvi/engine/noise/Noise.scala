package com.systemvi.engine.noise

trait Noise[A] {
  def get(point: A): Float
}
