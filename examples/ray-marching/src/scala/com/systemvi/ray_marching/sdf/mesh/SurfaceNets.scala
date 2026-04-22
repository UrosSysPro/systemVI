package com.systemvi.ray_marching.sdf.mesh

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.engine
import com.systemvi.engine.shader.Primitive
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.ray_marching.keyboard.KeyboardToSDF
import com.systemvi.ray_marching.opengl.{InputEvent, *}
import com.systemvi.ray_marching.opengl.CursorMode.*
import com.systemvi.ray_marching.opengl.KeyAction.*
import com.systemvi.ray_marching.opengl.buffer.*
import com.systemvi.ray_marching.opengl.shader.Shader
import com.systemvi.ray_marching.opengl.utils.BufferBit.*
import com.systemvi.ray_marching.opengl.utils.Utils
import com.systemvi.ray_marching.sdf.*
import com.systemvi.ray_marching.sdf.mesh.*
import org.joml.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import com.systemvi.ray_marching.sdf.SDF
import org.joml.*

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

  private def range(size:Vector3i) = (for{
    i <- 0 until size.x
    j <- 0 until size.y
    k <- 0 until size.z
  } yield (i,j,k)).toList

  def sdfToMesh2(
                  sdf: SDF,
                  bounds: Bounds,
                  resolution: Vector3i = Vector3i(100,100,100),
                  isoValue: Float = 0f,
                  roundIterationSteps: Int = 0,
                  smoothNormals: Boolean = false,
                ): IO[Mesh2[VertexWithNormal]]={
    val step = Vector3f(bounds.max).sub(bounds.min).div(resolution.x.toFloat,resolution.y.toFloat,resolution.z.toFloat)
    val e = 0.0001f
    for {
      //generate triangles
      triangles <- range(resolution).map{ (i,j,k) => for {
        _ <- IO.cede
        triangles <- IO {
          val x = bounds.min.x + i * step.x
          val y = bounds.min.y + j * step.y
          val z = bounds.min.z + k * step.z

          val value = sdf.getValue(Vector3f(x, y, z))
          val dirX = sdf.getValue(Vector3f(x, y, z).add(Vector3f(step.x, 0, 0)))
          val dirY = sdf.getValue(Vector3f(x, y, z).add(Vector3f(0, step.y, 0)))
          val dirZ = sdf.getValue(Vector3f(x, y, z).add(Vector3f(0, 0, step.z)))

          var triangles = List.empty[(VertexWithNormal, VertexWithNormal, VertexWithNormal)]

          //positive dir
          if (value < 0 && dirX >= 0) {
            triangles :+= (
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(1, 0, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z - step.z / 2), Vector3f(1, 0, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y - step.y / 2, z + step.z / 2), Vector3f(1, 0, 0)),
            )
            triangles :+= (
              VertexWithNormal(Vector3f(x + step.x / 2, y - step.y / 2, z + step.z / 2), Vector3f(1, 0, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z - step.z / 2), Vector3f(1, 0, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y - step.y / 2, z - step.z / 2), Vector3f(1, 0, 0)),
            )
          }
          if (value < 0 && dirY >= 0) {
            triangles :+= (
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, 1, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z - step.z / 2), Vector3f(0, 1, 0)),
              VertexWithNormal(Vector3f(x - step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, 1, 0)),
            )
            triangles :+= (
              VertexWithNormal(Vector3f(x - step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, 1, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z - step.z / 2), Vector3f(0, 1, 0)),
              VertexWithNormal(Vector3f(x - step.x / 2, y + step.y / 2, z - step.z / 2), Vector3f(0, 1, 0)),
            )
          }
          if (value < 0 && dirZ >= 0) {
            triangles :+= (
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, 0, 1)),
              VertexWithNormal(Vector3f(x + step.x / 2, y - step.y / 2, z + step.z / 2), Vector3f(0, 0, 1)),
              VertexWithNormal(Vector3f(x - step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, 0, 1)),
            )
            triangles :+= (
              VertexWithNormal(Vector3f(x - step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, 0, 1)),
              VertexWithNormal(Vector3f(x + step.x / 2, y - step.y / 2, z + step.z / 2), Vector3f(0, 0, 1)),
              VertexWithNormal(Vector3f(x - step.x / 2, y - step.y / 2, z + step.z / 2), Vector3f(0, 0, 1)),
            )
          }
          //negative dir
          if (value >= 0 && dirX < 0) {
            triangles :+= (
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(-1, 0, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z - step.z / 2), Vector3f(-1, 0, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y - step.y / 2, z + step.z / 2), Vector3f(-1, 0, 0)),
            )
            triangles :+= (
              VertexWithNormal(Vector3f(x + step.x / 2, y - step.y / 2, z + step.z / 2), Vector3f(-1, 0, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z - step.z / 2), Vector3f(-1, 0, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y - step.y / 2, z - step.z / 2), Vector3f(-1, 0, 0)),
            )
          }
          if (value >= 0 && dirY < 0) {
            triangles :+= (
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, -1, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z - step.z / 2), Vector3f(0, -1, 0)),
              VertexWithNormal(Vector3f(x - step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, -1, 0)),
            )
            triangles :+= (
              VertexWithNormal(Vector3f(x - step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, -1, 0)),
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z - step.z / 2), Vector3f(0, -1, 0)),
              VertexWithNormal(Vector3f(x - step.x / 2, y + step.y / 2, z - step.z / 2), Vector3f(0, -1, 0)),
            )
          }
          if (value >= 0 && dirZ < 0) {
            triangles :+= (
              VertexWithNormal(Vector3f(x + step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, 0, -1)),
              VertexWithNormal(Vector3f(x + step.x / 2, y - step.y / 2, z + step.z / 2), Vector3f(0, 0, -1)),
              VertexWithNormal(Vector3f(x - step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, 0, -1)),
            )
            triangles :+= (
              VertexWithNormal(Vector3f(x - step.x / 2, y + step.y / 2, z + step.z / 2), Vector3f(0, 0, -1)),
              VertexWithNormal(Vector3f(x + step.x / 2, y - step.y / 2, z + step.z / 2), Vector3f(0, 0, -1)),
              VertexWithNormal(Vector3f(x - step.x / 2, y - step.y / 2, z + step.z / 2), Vector3f(0, 0, -1)),
            )
          }
          triangles
        }
      }yield triangles }.parSequence.map(_.flatten)

      _ <- if roundIterationSteps > 0 then
        triangles.parTraverse{ (p0,p1,p2) => IO{
          List(p0,p1,p2).foreach{ p =>
            for(i<-0 until roundIterationSteps){
              p.position.add(
                Vector3f(
                  sdf.getValue(Vector3f(p.position).add(Vector3f(e,0,0))) - sdf.getValue(Vector3f(p.position).sub(Vector3f(e,0,0))),
                  sdf.getValue(Vector3f(p.position).add(Vector3f(0,e,0))) - sdf.getValue(Vector3f(p.position).sub(Vector3f(0,e,0))),
                  sdf.getValue(Vector3f(p.position).add(Vector3f(0,0,e))) - sdf.getValue(Vector3f(p.position).sub(Vector3f(0,0,e))),
                ).normalize().mul(-sdf.getValue(Vector3f(p.position)))
              )
            }
          }
          val normal = Vector3f(p1.position).sub(p0.position).cross(Vector3f(p2.position).sub(Vector3f(p0.position)))
          List(p0,p1,p2).foreach{p=>
            if smoothNormals then
              p.normal.set(
                Vector3f(
                  sdf.getValue(Vector3f(p.position).add(Vector3f(e,0,0))) - sdf.getValue(Vector3f(p.position).sub(Vector3f(e,0,0))),
                  sdf.getValue(Vector3f(p.position).add(Vector3f(0,e,0))) - sdf.getValue(Vector3f(p.position).sub(Vector3f(0,e,0))),
                  sdf.getValue(Vector3f(p.position).add(Vector3f(0,0,e))) - sdf.getValue(Vector3f(p.position).sub(Vector3f(0,0,e))),
                ).normalize()
              )
            else
              p.normal.set(normal)
          }
        }}
      else
        IO.unit
    } yield Mesh2[VertexWithNormal](triangles)
  }
}
