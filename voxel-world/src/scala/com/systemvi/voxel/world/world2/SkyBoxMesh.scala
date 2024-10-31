package com.systemvi.voxel.world.world2

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute

class SkyBoxMesh {
  private val vertexArray=VertexArray()
  private val arrayBuffer=ArrayBuffer()
  private val elementBuffer=ElementsBuffer()

  vertexArray.bind()
  arrayBuffer.bind()
  elementBuffer.bind()

  arrayBuffer.setVertexAttributes(Array(VertexAttribute("aPos",3)))
  arrayBuffer.setData(Array(
    0,0,0,
    1,0,0,
    0,1,0,
    1,1,0,
    0,0,1,
    1,0,1,
    0,1,1,
    1,1,1,
  ))
  elementBuffer.setData(Array(
    0,1,2,
    1,2,3
  ))
  
  def bind(): Unit = arrayBuffer.bind()
}
