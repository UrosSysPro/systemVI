package com.systemvi.ray_marching.sdf.mesh

import com.systemvi.ray_marching.sdf.SDF
import org.joml.Vector3f

case class Bounds(min: Vector3f, max: Vector3f)

case class Mesh(
               vertices: List[Float],
               indices: List[Int]
               )

object SurfaceNets {
  private val CORNERS = List(
    (0,0,0),(1,0,0),(0,1,0),(1,1,0),
    (0,0,1),(1,0,1),(0,1,1),(1,1,1)
  )

  private val edges = List(
    (0,1),(2,3),(4,5),(6,7),   // edges along X
    (0,2),(1,3),(4,6),(5,7),   // edges along Y
    (0,4),(1,5),(2,6),(3,7)    // edges along Z
  )
  private val edge_neighbors: Array[Array[(Int, Int, Int)]] = Array(
    // axis 0 = X-edge  → quad in YZ-plane
    Array((0, 0, 0), (0, -1, 0), (0, -1, -1), (0, 0, -1)),
    // axis 1 = Y-edge  → quad in XZ-plane
    Array((0, 0, 0), (0, 0, -1), (-1, 0, -1), (-1, 0, 0)),
    // axis 2 = Z-edge  → quad in XY-plane
    Array((0, 0, 0), (-1, 0, 0), (-1, -1, 0), (0, -1, 0))
  )

  def sdfToMesh(sdf: SDF, bounds: Bounds, resolution: Int = 64, isoValue: Float = 0f) = {
    val nx = resolution + 1 // grid *vertices* per axis
    val ny = resolution + 1
    val nz = resolution + 1

    val dx = (bounds.max.x - bounds.min.x) / resolution
    val dy = (bounds.max.y - bounds.min.y) / resolution
    val dz = (bounds.max.z - bounds.min.z) / resolution

    val samples = Array.ofDim[Float](nx, ny, nz)
    for (ix <- 0 until nx; iy <- 0 until ny; iz <- 0 until nz) {
      val p = new Vector3f(
        bounds.min.x + ix * dx,
        bounds.min.y + iy * dy,
        bounds.min.z + iz * dz
      )
      samples(ix)(iy)(iz) = sdf.getValue(p) - isoValue
    }

    val vertexIndex = Array.fill(nx - 1, ny - 1, nz - 1)(-1)
    val vertexPositions = scala.collection.mutable.ArrayBuffer.empty[Float]

    for (ix <- 0 until nx - 1; iy <- 0 until ny - 1; iz <- 0 until nz - 1) {

      val corners = CORNERS.map { case (cx, cy, cz) =>
        samples(ix + cx)(iy + cy)(iz + cz)
      }

      val inside = corners.map(_ < 0f)
      val anyIn = inside.exists(identity)
      val anyOut = inside.exists(!_)

      if (anyIn && anyOut) {
        var sumX = 0f
        var sumY = 0f
        var sumZ = 0f
        var count = 0

        for ((a, b) <- edges) {
          if (inside(a) != inside(b)) {
            val va = corners(a)
            val vb = corners(b)
            val t = va / (va - vb)

            val (cax, cay, caz) = CORNERS(a)
            val (cbx, cby, cbz) = CORNERS(b)

            sumX += bounds.min.x + (ix + cax + t * (cbx - cax)) * dx
            sumY += bounds.min.y + (iy + cay + t * (cby - cay)) * dy
            sumZ += bounds.min.z + (iz + caz + t * (cbz - caz)) * dz
            count += 1
          }
        }

        val vid = vertexPositions.length / 3
        vertexIndex(ix)(iy)(iz) = vid
        vertexPositions += sumX / count
        vertexPositions += sumY / count
        vertexPositions += sumZ / count
      }
    }

    val triangleIndices = scala.collection.mutable.ArrayBuffer.empty[Int]

    def safeVid(ix: Int, iy: Int, iz: Int): Int =
      if (ix < 0 || iy < 0 || iz < 0 ||
        ix >= nx - 1 || iy >= ny - 1 || iz >= nz - 1) -1
      else vertexIndex(ix)(iy)(iz)

    for (ix <- 1 until nx - 1; iy <- 1 until ny - 1; iz <- 1 until nz - 1) {
      for (axis <- 0 until 3) {
        val (ex, ey, ez) = axis match {
          case 0 => (ix + 1, iy, iz)
          case 1 => (ix, iy + 1, iz)
          case _ => (ix, iy, iz + 1)
        }

        if (ex < nx && ey < ny && ez < nz) {
          val s0 = samples(ix)(iy)(iz)
          val s1 = samples(ex)(ey)(ez)

          if ((s0 < 0f) != (s1 < 0f)) {
            val vids = edge_neighbors(axis).map { case (ddx, ddy, ddz) =>
              safeVid(ix + ddx, iy + ddy, iz + ddz)
            }

            if (vids.forall(_ >= 0)) {
              val (v0, v1, v2, v3) =
                if (s0 < 0f) (vids(0), vids(1), vids(2), vids(3))
                else (vids(3), vids(2), vids(1), vids(0))

              triangleIndices += v0
              triangleIndices += v1
              triangleIndices += v2
              triangleIndices += v0
              triangleIndices += v2
              triangleIndices += v3
            }
          }
        }
      }
    }

    val mesh = Mesh(
      vertices = vertexPositions.toList,
      indices = triangleIndices.toList
    )
    val v = mesh.vertices.map(identity).toArray
    val eps = 1e-4f
    val iterations = 10
    val stepSize = 0.1f
    for (_ <- 0 until iterations) {
      for (i <- 0 until v.length / 3) {
        val p = new Vector3f(v(i * 3), v(i * 3 + 1), v(i * 3 + 2))
        val d = sdf.getValue(p)
        // Numerical gradient
        val gx = (sdf.getValue(new Vector3f(p.x + eps, p.y, p.z)) - sdf.getValue(new Vector3f(p.x - eps, p.y, p.z))) / (2 * eps)
        val gy = (sdf.getValue(new Vector3f(p.x, p.y + eps, p.z)) - sdf.getValue(new Vector3f(p.x, p.y - eps, p.z))) / (2 * eps)
        val gz = (sdf.getValue(new Vector3f(p.x, p.y, p.z + eps)) - sdf.getValue(new Vector3f(p.x, p.y, p.z - eps))) / (2 * eps)
        // Project back onto surface along gradient
        v(i * 3) -= gx * d * stepSize
        v(i * 3 + 1) -= gy * d * stepSize
        v(i * 3 + 2) -= gz * d * stepSize
      }
    }
    mesh.copy(vertices = v.toList)
  }
}
