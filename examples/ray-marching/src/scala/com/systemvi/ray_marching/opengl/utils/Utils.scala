package com.systemvi.ray_marching.opengl.utils

import org.joml.Vector4f
import org.lwjgl.opengl.GL11.*

enum BufferBit(val id: Int) {
  case ColorBit extends BufferBit(GL_COLOR_BUFFER_BIT)
  case DepthBit extends BufferBit(GL_DEPTH_BUFFER_BIT)
  case StencilBit extends BufferBit(GL_STENCIL_BUFFER_BIT)
}

object Utils {
  def clear(color:Vector4f, bufferBits: BufferBit*)={
    glClearColor(color.x,color.y,color.z,color.w)
    val bits = bufferBits.foldLeft(0){(acc,bit) => acc|bit.id}
    glClear(bits)
  }
}
