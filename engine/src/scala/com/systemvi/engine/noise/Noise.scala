package com.systemvi.engine.noise

import org.joml.{Matrix3f, Matrix4f, Vector2f, Vector3f, Vector4f}

trait Noise[A] {
  def get(point: A): Float
}

class Octave2(noise:Noise[Vector2f],transform: Matrix4f=Matrix4f()){
  private val vector2:Vector2f=Vector2f()
  private val vector4:Vector4f=Vector4f()
  def get(point:Vector2f):Float={
    vector4.set(point,0,1)
    vector4.mul(transform)
    vector2.set(vector4.x,vector4.y)
    noise.get(vector2)
  }
}
