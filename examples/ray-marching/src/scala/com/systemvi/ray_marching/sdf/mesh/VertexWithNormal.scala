package com.systemvi.ray_marching.sdf.mesh

import org.joml.Vector3f

case class VertexWithNormal(position: Vector3f, normal: Vector3f)

extension (mesh: Mesh2[VertexWithNormal]){
  def toArray: Array[Float] = mesh.triangles.flatMap{ (p1,p2,p3) =>
    Array(p1,p2,p3).flatMap{p=>Array(
      p.position.x,p.position.y,p.position.z,
      p.normal.x,p.normal.y,p.normal.z
    )}
  }.toArray
}