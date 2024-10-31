package com.systemvi.voxel.world.buffer

import com.systemvi.engine.texture.Texture.{FilterMag, FilterMin}
import com.systemvi.engine.texture.{Format, FrameBuffer, Texture}

class GBuffer(width: Int, height: Int) {
//  val formats: Tuple5[Format,Format,Format,Format,Format] = (Format.RGB32F,Format.RGB32F,Format.RG16F,Format.R16F,Format.DEPTH32)
//  val (position,normal,uv,occlusion,depth)=formats.map(
//    [T] =>(a:Format)=> { a match
//      case format: Format =>
//        Texture.builder()
//          .width(width)
//          .height(height)
//          .format(Format.RGB32F)
//          .filterMag(FilterMag.NEAREST)
//          .filterMin(FilterMin.LINEAR)
//          .build()
//    }
//  )

  val position: Texture = Texture.builder()
    .width(width)
    .height(height)
    .format(Format.RGB32F)
    .filterMag(FilterMag.NEAREST)
    .filterMin(FilterMin.LINEAR)
    .build()
  val normal: Texture = Texture.builder()
    .width(width)
    .height(height)
    .format(Format.RGB32F)
    .filterMag(FilterMag.NEAREST)
    .filterMin(FilterMin.LINEAR)
    .build()
  val tangent: Texture = Texture.builder()
    .width(width)
    .height(height)
    .format(Format.RGB32F)
    .filterMag(FilterMag.NEAREST)
    .filterMin(FilterMin.LINEAR)
    .build()
  val uv: Texture = Texture.builder()
    .width(width)
    .height(height)
    .format(Format.RG16F)
    .filterMag(FilterMag.NEAREST)
    .filterMin(FilterMin.LINEAR)
    .build()
  val occlusion: Texture = Texture.builder()
    .width(width)
    .height(height)
    .format(Format.R16F)
    .filterMag(FilterMag.NEAREST)
    .filterMin(FilterMin.LINEAR)
    .build()
  val depth: Texture = Texture.builder()
    .width(width)
    .height(height)
    .filterMag(FilterMag.NEAREST)
    .filterMin(FilterMin.LINEAR)
    .format(Format.DEPTH32)
    .build()

  val frameBuffer = FrameBuffer.builder()
    .color(position)
    .color(normal)
    .color(tangent)
    .color(uv)
    .color(occlusion)
    .depth(depth)
    .build()

  def begin(): Unit = frameBuffer.begin()

  def end(): Unit = frameBuffer.end()

  def bind(): Unit =
    position.bind(0)
    normal.bind(1)
    tangent.bind(2)
    uv.bind(3)
    occlusion.bind(4)
    depth.bind(5)
}
