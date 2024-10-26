package com.systemvi.voxel.world.world2

import org.joml.Vector3i

class WorldCache(world: World) {
  val chunkCache:Array[Array[Array[ChunkCache]]]=Array.ofDim(
    world.chunks.length,
    world.chunks(0).length,
    world.chunks(0)(0).length,
  )
  for(i<-chunkCache.indices;j<-chunkCache(0).indices;k<-chunkCache(0)(0).indices){
    chunkCache(i)(j)(k)=ChunkCache(world.chunks(i)(j)(k),Vector3i(i,j,k))
  }

}
