package com.systemvi.voxel.world.buffer

import com.systemvi.engine.texture.{Format, FrameBuffer, Texture}

class GBuffer(width: Int, height: Int) {
  val position: Texture = Texture(width, height, Format.RGB32F)
  val normal: Texture = Texture(width, height, Format.RGB32F)
  val uv: Texture = Texture(width, height, Format.RG16F)
  val depth: Texture = Texture(width, height, Format.DEPTH24)

  val frameBuffer = FrameBuffer.builder()
    .color(position)
    .color(normal)
    .color(uv)
    .depth(depth)
    .build()

  def begin(): Unit = frameBuffer.begin()

  def end(): Unit = frameBuffer.end()
  
  def bind(): Unit =
    position.bind(0)
    normal.bind(1)
    uv.bind(2)
    depth.bind(3)
}
