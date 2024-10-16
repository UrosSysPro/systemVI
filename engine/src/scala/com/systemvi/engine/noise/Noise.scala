package com.systemvi.engine.noise

import org.joml.{ Vector2f, Vector4f }

trait Noise[A] {
  def get(point: A): Float
}

class Octave2(noise:Noise[Vector2f],frequency:Float=1,amplitude:Float=1,offset:Vector2f=Vector2f()){
  private val v=Vector2f()
  def get(point:Vector2f):Float={
    noise.get(v.set(point).mul(frequency).add(offset))*amplitude
  }
}
