package com.systemvi.ray_marching.sdf.mesh
import com.systemvi.ray_marching.sdf.SDF
import org.joml.{Vector3f, Vector3i}

/**
 * Marching Cubes meshing algorithm.
 *
 * Produces the same TriangleMesh output as SurfaceNets:
 *   vertices: Array[Float]  interleaved XYZ, length = numVertices * 3
 *   indices:  Array[Int]    index triples,   length = numTriangles * 3
 *
 * Vertices on shared edges are deduplicated via a HashMap so the output
 * index buffer is valid for indexed rendering (no duplicated positions).
 *
 * Pipeline:
 *  1. Sample SDF on a regular grid.
 *  2. For each cell, build an 8-bit case index from corner signs.
 *  3. Look up active edges in EDGE_TABLE, triangles in TRIANGLE_TABLE.
 *  4. Linearly interpolate each active edge crossing → world-space vertex.
 *  5. Deduplicate via edge key → vertex index map, emit index triples.
 */
object MarchingCubes {
  // ---------------------------------------------------------------------------
  // Cube layout
  //
  //     6 ---- 7
  //    /|     /|
  //   4 ---- 5 |
  //   | 2 -- | 3
  //   |/     |/
  //   0 ---- 1
  //
  // Corner bit encoding: bit0=x, bit1=y, bit2=z
  //   0=(0,0,0) 1=(1,0,0) 2=(0,1,0) 3=(1,1,0)
  //   4=(0,0,1) 5=(1,0,1) 6=(0,1,1) 7=(1,1,1)
  //
  // Edge numbering (Bourke/Lorensen standard):
  //   0:0-1  1:1-2  2:2-3  3:3-0   (bottom face)
  //   4:4-5  5:5-6  6:6-7  7:7-4   (top face)
  //   8:0-4  9:1-5 10:2-6 11:3-7   (vertical)
  // ---------------------------------------------------------------------------

  private val CORNER_OFFSETS: Array[(Int,Int,Int)] = Array(
    (0,0,0),(1,0,0),(1,1,0),(0,1,0),
    (0,0,1),(1,0,1),(1,1,1),(0,1,1)
  )

  private val EDGE_CORNERS: Array[(Int,Int)] = Array(
    (0,1),(1,2),(2,3),(3,0),
    (4,5),(5,6),(6,7),(7,4),
    (0,4),(1,5),(2,6),(3,7)
  )

  // ---------------------------------------------------------------------------
  // Standard MC lookup tables (Lorensen & Cline 1987 / Paul Bourke)
  // ---------------------------------------------------------------------------

  private val EDGE_TABLE: Array[Int] = Array(
    0x000,0x109,0x203,0x30a,0x406,0x50f,0x605,0x70c,
    0x80c,0x905,0xa0f,0xb06,0xc0a,0xd03,0xe09,0xf00,
    0x190,0x099,0x393,0x29a,0x596,0x49f,0x795,0x69c,
    0x99c,0x895,0xb9f,0xa96,0xd9a,0xc93,0xf99,0xe90,
    0x230,0x339,0x033,0x13a,0x636,0x73f,0x435,0x53c,
    0xa3c,0xb35,0x83f,0x936,0xe3a,0xf33,0xc39,0xd30,
    0x3a0,0x2a9,0x1a3,0x0aa,0x7a6,0x6af,0x5a5,0x4ac,
    0xbac,0xaa5,0x9af,0x8a6,0xfaa,0xea3,0xda9,0xca0,
    0x460,0x569,0x663,0x76a,0x066,0x16f,0x265,0x36c,
    0xc6c,0xd65,0xe6f,0xf66,0x86a,0x963,0xa69,0xb60,
    0x5f0,0x4f9,0x7f3,0x6fa,0x1f6,0x0ff,0x3f5,0x2fc,
    0xdfc,0xcf5,0xfff,0xef6,0x9fa,0x8f3,0xbf9,0xaf0,
    0x650,0x759,0x453,0x55a,0x256,0x35f,0x055,0x15c,
    0xe5c,0xf55,0xc5f,0xd56,0xa5a,0xb53,0x859,0x950,
    0x7c0,0x6c9,0x5c3,0x4ca,0x3c6,0x2cf,0x1c5,0x0cc,
    0xfcc,0xec5,0xdcf,0xcc6,0xbca,0xac3,0x9c9,0x8c0,
    0x8c0,0x9c9,0xac3,0xbca,0xcc6,0xdcf,0xec5,0xfcc,
    0x0cc,0x1c5,0x2cf,0x3c6,0x4ca,0x5c3,0x6c9,0x7c0,
    0x950,0x859,0xb53,0xa5a,0xd56,0xc5f,0xf55,0xe5c,
    0x15c,0x055,0x35f,0x256,0x55a,0x453,0x759,0x650,
    0xaf0,0xbf9,0x8f3,0x9fa,0xef6,0xfff,0xcf5,0xdfc,
    0x2fc,0x3f5,0x0ff,0x1f6,0x6fa,0x7f3,0x4f9,0x5f0,
    0xb60,0xa69,0x963,0x86a,0xf66,0xe6f,0xd65,0xc6c,
    0x36c,0x265,0x16f,0x066,0x76a,0x663,0x569,0x460,
    0xca0,0xda9,0xea3,0xfaa,0x8a6,0x9af,0xaa5,0xbac,
    0x4ac,0x5a5,0x6af,0x7a6,0x0aa,0x1a3,0x2a9,0x3a0,
    0xd30,0xc39,0xf33,0xe3a,0x936,0x83f,0xb35,0xa3c,
    0x53c,0x435,0x73f,0x636,0x13a,0x033,0x339,0x230,
    0xe90,0xf99,0xc93,0xd9a,0xa96,0xb9f,0x895,0x99c,
    0x69c,0x795,0x49f,0x596,0x29a,0x393,0x099,0x190,
    0xf00,0xe09,0xd03,0xc0a,0xb06,0xa0f,0x905,0x80c,
    0x70c,0x605,0x50f,0x406,0x30a,0x203,0x109,0x000
  )

  private val TRIANGLE_TABLE: Array[Array[Int]] = Array(
    Array(-1),
    Array(0,8,3,-1),
    Array(0,1,9,-1),
    Array(1,8,3,9,8,1,-1),
    Array(1,2,10,-1),
    Array(0,8,3,1,2,10,-1),
    Array(9,2,10,0,2,9,-1),
    Array(2,8,3,2,10,8,10,9,8,-1),
    Array(3,11,2,-1),
    Array(0,11,2,8,11,0,-1),
    Array(1,9,0,2,3,11,-1),
    Array(1,11,2,1,9,11,9,8,11,-1),
    Array(3,10,1,11,10,3,-1),
    Array(0,10,1,0,8,10,8,11,10,-1),
    Array(3,9,0,3,11,9,11,10,9,-1),
    Array(9,8,10,10,8,11,-1),
    Array(4,7,8,-1),
    Array(4,3,0,7,3,4,-1),
    Array(0,1,9,8,4,7,-1),
    Array(4,1,9,4,7,1,7,3,1,-1),
    Array(1,2,10,8,4,7,-1),
    Array(3,4,7,3,0,4,1,2,10,-1),
    Array(9,2,10,9,0,2,8,4,7,-1),
    Array(2,10,9,2,9,7,2,7,3,7,9,4,-1),
    Array(8,4,7,3,11,2,-1),
    Array(11,4,7,11,2,4,2,0,4,-1),
    Array(9,0,1,8,4,7,2,3,11,-1),
    Array(4,7,11,9,4,11,9,11,2,9,2,1,-1),
    Array(3,10,1,3,11,10,7,8,4,-1),
    Array(1,11,10,1,4,11,1,0,4,7,11,4,-1),
    Array(4,7,8,9,0,11,9,11,10,11,0,3,-1),
    Array(4,7,11,4,11,9,9,11,10,-1),
    Array(9,5,4,-1),
    Array(9,5,4,0,8,3,-1),
    Array(0,5,4,1,5,0,-1),
    Array(8,5,4,8,3,5,3,1,5,-1),
    Array(1,2,10,9,5,4,-1),
    Array(3,0,8,1,2,10,4,9,5,-1),
    Array(5,2,10,5,4,2,4,0,2,-1),
    Array(2,10,5,3,2,5,3,5,4,3,4,8,-1),
    Array(9,5,4,2,3,11,-1),
    Array(0,11,2,0,8,11,4,9,5,-1),
    Array(0,5,4,0,1,5,2,3,11,-1),
    Array(2,1,5,2,5,8,2,8,11,4,8,5,-1),
    Array(10,3,11,10,1,3,9,5,4,-1),
    Array(4,9,5,0,8,1,8,10,1,8,11,10,-1),
    Array(5,4,0,5,0,11,5,11,10,11,0,3,-1),
    Array(5,4,8,5,8,10,10,8,11,-1),
    Array(9,7,8,5,7,9,-1),
    Array(9,3,0,9,5,3,5,7,3,-1),
    Array(0,7,8,0,1,7,1,5,7,-1),
    Array(1,5,3,3,5,7,-1),
    Array(9,7,8,9,5,7,10,1,2,-1),
    Array(10,1,2,9,5,0,5,3,0,5,7,3,-1),
    Array(8,0,2,8,2,5,8,5,7,10,5,2,-1),
    Array(2,10,5,2,5,3,3,5,7,-1),
    Array(7,9,5,7,8,9,3,11,2,-1),
    Array(9,5,7,9,7,2,9,2,0,2,7,11,-1),
    Array(2,3,11,0,1,8,1,7,8,1,5,7,-1),
    Array(11,2,1,11,1,7,7,1,5,-1),
    Array(9,5,8,8,5,7,10,1,3,10,3,11,-1),
    Array(5,7,0,5,0,9,7,11,0,1,0,10,11,10,0,-1),
    Array(11,10,0,11,0,3,10,5,0,8,0,7,5,7,0,-1),
    Array(11,10,5,7,11,5,-1),
    Array(10,6,5,-1),
    Array(0,8,3,5,10,6,-1),
    Array(9,0,1,5,10,6,-1),
    Array(1,8,3,1,9,8,5,10,6,-1),
    Array(1,6,5,2,6,1,-1),
    Array(1,6,5,1,2,6,3,0,8,-1),
    Array(9,6,5,9,0,6,0,2,6,-1),
    Array(5,9,8,5,8,2,5,2,6,3,2,8,-1),
    Array(2,3,11,10,6,5,-1),
    Array(11,0,8,11,2,0,10,6,5,-1),
    Array(0,1,9,2,3,11,5,10,6,-1),
    Array(5,10,6,1,9,2,9,11,2,9,8,11,-1),
    Array(6,3,11,6,5,3,5,1,3,-1),
    Array(0,8,11,0,11,5,0,5,1,5,11,6,-1),
    Array(3,11,6,0,3,6,0,6,5,0,5,9,-1),
    Array(6,5,9,6,9,11,11,9,8,-1),
    Array(5,10,6,4,7,8,-1),
    Array(4,3,0,4,7,3,6,5,10,-1),
    Array(1,9,0,5,10,6,8,4,7,-1),
    Array(10,6,5,1,9,7,1,7,3,7,9,4,-1),
    Array(6,1,2,6,5,1,4,7,8,-1),
    Array(1,2,5,5,2,6,3,0,4,3,4,7,-1),
    Array(8,4,7,9,0,5,0,6,5,0,2,6,-1),
    Array(7,3,9,7,9,4,3,2,9,5,9,6,2,6,9,-1),
    Array(3,11,2,7,8,4,10,6,5,-1),
    Array(5,10,6,4,7,2,4,2,0,2,7,11,-1),
    Array(0,1,9,4,7,8,2,3,11,5,10,6,-1),
    Array(9,2,1,9,11,2,9,4,11,7,11,4,5,10,6,-1),
    Array(8,4,7,3,11,5,3,5,1,5,11,6,-1),
    Array(5,1,11,5,11,6,1,0,11,7,11,4,0,4,11,-1),
    Array(0,5,9,0,6,5,0,3,6,11,6,3,8,4,7,-1),
    Array(6,5,9,6,9,11,4,7,9,7,11,9,-1),
    Array(10,4,9,6,4,10,-1),
    Array(4,10,6,4,9,10,0,8,3,-1),
    Array(10,0,1,10,6,0,6,4,0,-1),
    Array(8,3,1,8,1,6,8,6,4,6,1,10,-1),
    Array(1,4,9,1,2,4,2,6,4,-1),
    Array(3,0,8,1,2,9,2,4,9,2,6,4,-1),
    Array(0,2,4,4,2,6,-1),
    Array(8,3,2,8,2,4,4,2,6,-1),
    Array(10,4,9,10,6,4,11,2,3,-1),
    Array(0,8,2,2,8,11,4,9,10,4,10,6,-1),
    Array(3,11,2,0,1,6,0,6,4,6,1,10,-1),
    Array(6,4,1,6,1,10,4,8,1,2,1,11,8,11,1,-1),
    Array(9,6,4,9,3,6,9,1,3,11,6,3,-1),
    Array(8,11,1,8,1,0,11,6,1,9,1,4,6,4,1,-1),
    Array(3,11,6,3,6,0,0,6,4,-1),
    Array(6,4,8,11,6,8,-1),
    Array(7,10,6,7,8,10,8,9,10,-1),
    Array(0,7,3,0,10,7,0,9,10,6,7,10,-1),
    Array(10,6,7,1,10,7,1,7,8,1,8,0,-1),
    Array(10,6,7,10,7,1,1,7,3,-1),
    Array(1,2,6,1,6,8,1,8,9,8,6,7,-1),
    Array(2,6,9,2,9,1,6,7,9,0,9,3,7,3,9,-1),
    Array(7,8,0,7,0,6,6,0,2,-1),
    Array(7,3,2,6,7,2,-1),
    Array(2,3,11,10,6,8,10,8,9,8,6,7,-1),
    Array(2,0,7,2,7,11,0,9,7,6,7,10,9,10,7,-1),
    Array(1,8,0,1,7,8,1,10,7,6,7,10,2,3,11,-1),
    Array(11,2,1,11,1,7,10,6,1,6,7,1,-1),
    Array(8,9,6,8,6,7,9,1,6,11,6,3,1,3,6,-1),
    Array(0,9,1,11,6,7,-1),
    Array(7,8,0,7,0,6,3,11,0,11,6,0,-1),
    Array(7,11,6,-1),
    Array(7,6,11,-1),
    Array(3,0,8,11,7,6,-1),
    Array(0,1,9,11,7,6,-1),
    Array(8,1,9,8,3,1,11,7,6,-1),
    Array(10,1,2,6,11,7,-1),
    Array(1,2,10,3,0,8,6,11,7,-1),
    Array(2,9,0,2,10,9,6,11,7,-1),
    Array(6,11,7,2,10,3,10,8,3,10,9,8,-1),
    Array(7,2,3,6,2,7,-1),
    Array(7,0,8,7,6,0,6,2,0,-1),
    Array(2,7,6,2,3,7,0,1,9,-1),
    Array(1,6,2,1,8,6,1,9,8,8,7,6,-1),
    Array(10,7,6,10,1,7,1,3,7,-1),
    Array(10,7,6,1,7,10,1,8,7,1,0,8,-1),
    Array(0,3,7,0,7,10,0,10,9,6,10,7,-1),
    Array(7,6,10,7,10,8,8,10,9,-1),
    Array(6,8,4,11,8,6,-1),
    Array(3,6,11,3,0,6,0,4,6,-1),
    Array(8,6,11,8,4,6,9,0,1,-1),
    Array(9,4,6,9,6,3,9,3,1,11,3,6,-1),
    Array(6,8,4,6,11,8,2,10,1,-1),
    Array(1,2,10,3,0,11,0,6,11,0,4,6,-1),
    Array(4,11,8,4,6,11,0,2,9,2,10,9,-1),
    Array(10,9,3,10,3,2,9,4,3,11,3,6,4,6,3,-1),
    Array(8,2,3,8,4,2,4,6,2,-1),
    Array(0,4,2,4,6,2,-1),
    Array(1,9,0,2,3,4,2,4,6,4,3,8,-1),
    Array(1,9,4,1,4,2,2,4,6,-1),
    Array(8,1,3,8,6,1,8,4,6,6,10,1,-1),
    Array(10,1,0,10,0,6,6,0,4,-1),
    Array(4,6,3,4,3,8,6,10,3,0,3,9,10,9,3,-1),
    Array(10,9,4,6,10,4,-1),
    Array(4,9,5,7,6,11,-1),
    Array(0,8,3,4,9,5,11,7,6,-1),
    Array(5,0,1,5,4,0,7,6,11,-1),
    Array(11,7,6,8,3,4,3,5,4,3,1,5,-1),
    Array(9,5,4,10,1,2,7,6,11,-1),
    Array(6,11,7,1,2,10,0,8,3,4,9,5,-1),
    Array(7,6,11,5,4,10,4,2,10,4,0,2,-1),
    Array(3,4,8,3,5,4,3,2,5,10,5,2,11,7,6,-1),
    Array(7,2,3,7,6,2,5,4,9,-1),
    Array(9,5,4,0,8,6,0,6,2,6,8,7,-1),
    Array(3,6,2,3,7,6,1,5,0,5,4,0,-1),
    Array(6,2,8,6,8,7,2,1,8,4,8,5,1,5,8,-1),
    Array(9,5,4,10,1,6,1,7,6,1,3,7,-1),
    Array(1,6,10,1,7,6,1,0,7,8,7,0,9,5,4,-1),
    Array(4,0,10,4,10,5,0,3,10,6,10,7,3,7,10,-1),
    Array(7,6,10,7,10,8,5,4,10,4,8,10,-1),
    Array(6,9,5,6,11,9,11,8,9,-1),
    Array(3,6,11,0,6,3,0,5,6,0,9,5,-1),
    Array(0,11,8,0,5,11,0,1,5,5,6,11,-1),
    Array(6,11,3,6,3,5,5,3,1,-1),
    Array(1,2,10,9,5,11,9,11,8,11,5,6,-1),
    Array(0,11,3,0,6,11,0,9,6,5,6,9,1,2,10,-1),
    Array(11,8,5,11,5,6,8,0,5,10,5,2,0,2,5,-1),
    Array(6,11,3,6,3,5,2,10,3,10,5,3,-1),
    Array(5,8,9,5,2,8,5,6,2,3,8,2,-1),
    Array(9,5,6,9,6,0,0,6,2,-1),
    Array(1,5,8,1,8,0,5,6,8,3,8,2,6,2,8,-1),
    Array(1,5,6,2,1,6,-1),
    Array(1,3,6,1,6,10,3,8,6,5,6,9,8,9,6,-1),
    Array(10,1,0,10,0,6,9,5,0,5,6,0,-1),
    Array(0,3,8,5,6,10,-1),
    Array(10,5,6,-1),
    Array(11,5,10,7,5,11,-1),
    Array(11,5,10,11,7,5,8,3,0,-1),
    Array(5,11,7,5,10,11,1,9,0,-1),
    Array(10,7,5,10,11,7,9,8,1,8,3,1,-1),
    Array(11,1,2,11,7,1,7,5,1,-1),
    Array(0,8,3,1,2,7,1,7,5,7,2,11,-1),
    Array(9,7,5,9,2,7,9,0,2,2,11,7,-1),
    Array(7,5,2,7,2,11,5,9,2,3,2,8,9,8,2,-1),
    Array(2,5,10,2,3,5,3,7,5,-1),
    Array(8,2,0,8,5,2,8,7,5,10,2,5,-1),
    Array(9,0,1,2,3,5,3,7,5,-1),
    Array(9,8,2,9,2,1,8,7,2,10,2,5,7,5,2,-1),
    Array(1,3,5,3,7,5,-1),
    Array(0,8,7,0,7,1,1,7,5,-1),
    Array(9,0,3,9,3,5,5,3,7,-1),
    Array(9,8,7,5,9,7,-1),
    Array(5,8,4,5,10,8,10,11,8,-1),
    Array(5,0,4,5,11,0,5,10,11,11,3,0,-1),
    Array(0,1,9,8,4,10,8,10,11,10,4,5,-1),
    Array(10,11,4,10,4,5,11,3,4,9,4,1,3,1,4,-1),
    Array(2,5,1,2,8,5,2,11,8,4,5,8,-1),
    Array(0,4,11,0,11,3,4,5,11,2,11,1,5,1,11,-1),
    Array(0,2,5,0,5,9,2,11,5,4,5,8,11,8,5,-1),
    Array(9,4,5,2,11,3,-1),
    Array(2,5,10,3,5,2,3,4,5,3,8,4,-1),
    Array(5,10,2,5,2,4,4,2,0,-1),
    Array(3,10,2,3,5,10,3,8,5,4,5,8,0,1,9,-1),
    Array(5,10,2,5,2,4,1,9,2,9,4,2,-1),
    Array(8,4,5,8,5,3,3,5,1,-1),
    Array(0,4,5,1,0,5,-1),
    Array(8,4,5,8,5,3,9,0,5,0,3,5,-1),
    Array(9,4,5,-1),
    Array(4,11,7,4,9,11,9,10,11,-1),
    Array(0,8,3,4,9,7,9,11,7,9,10,11,-1),
    Array(1,10,11,1,11,4,1,4,0,7,4,11,-1),
    Array(3,1,4,3,4,8,1,10,4,7,4,11,10,11,4,-1),
    Array(4,11,7,9,11,4,9,2,11,9,1,2,-1),
    Array(9,7,4,9,11,7,9,1,11,2,11,1,0,8,3,-1),
    Array(11,7,4,11,4,2,2,4,0,-1),
    Array(11,7,4,11,4,2,8,3,4,3,2,4,-1),
    Array(2,9,10,2,7,9,2,3,7,7,4,9,-1),
    Array(9,10,7,9,7,4,10,2,7,8,7,0,2,0,7,-1),
    Array(3,7,10,3,10,2,7,4,10,1,10,0,4,0,10,-1),
    Array(1,10,2,8,7,4,-1),
    Array(4,9,1,4,1,7,7,1,3,-1),
    Array(4,9,1,4,1,7,0,8,1,8,7,1,-1),
    Array(4,0,3,7,4,3,-1),
    Array(4,8,7,-1),
    Array(9,10,8,10,11,8,-1),
    Array(3,0,9,3,9,11,11,9,10,-1),
    Array(0,1,10,0,10,8,8,10,11,-1),
    Array(3,1,10,11,3,10,-1),
    Array(1,2,11,1,11,9,9,11,8,-1),
    Array(3,0,9,3,9,11,1,2,9,2,11,9,-1),
    Array(0,2,11,8,0,11,-1),
    Array(3,2,11,-1),
    Array(2,3,8,2,8,10,10,8,9,-1),
    Array(9,10,2,0,9,2,-1),
    Array(2,3,8,2,8,10,0,1,8,1,10,8,-1),
    Array(1,10,2,-1),
    Array(1,3,8,9,1,8,-1),
    Array(0,9,1,-1),
    Array(0,3,8,-1),
    Array(-1)
  )

  // ---------------------------------------------------------------------------
  // Main entry point
  // ---------------------------------------------------------------------------

  /**
   * Convert an SDF to a triangle mesh using Marching Cubes.
   *
   * @param sdf        Signed-distance function; negative = inside.
   * @param bounds     World-space AABB to mesh.
   * @param resolution Number of grid *cells* along each axis.
   * @param isoValue   Iso-level to extract (default 0).
   */
  def sdfToMesh(
                 sdf: SDF,
                 bounds: Bounds,
                 resolution: Int = 64,
                 isoValue: Float = 0f
                   ): Mesh = {

    val nx = resolution + 1
    val ny = resolution + 1
    val nz = resolution + 1

    val dx = (bounds.max.x - bounds.min.x) / resolution
    val dy = (bounds.max.y - bounds.min.y) / resolution
    val dz = (bounds.max.z - bounds.min.z) / resolution

    // Step 1: sample SDF at every grid vertex
    val samples = Array.ofDim[Float](nx, ny, nz)
    for (ix <- 0 until nx; iy <- 0 until ny; iz <- 0 until nz) {
      samples(ix)(iy)(iz) = sdf.getValue(new Vector3f(
        bounds.min.x + ix * dx,
        bounds.min.y + iy * dy,
        bounds.min.z + iz * dz
      )) - isoValue
    }

    // Vertex deduplication: edge → vertex index
    // Edge key encodes both endpoint grid-vertex flat-indices (order-independent)
    val totalGridVerts = nx * ny * nz
    val vertexPositions = scala.collection.mutable.ArrayBuffer.empty[Float]
    val edgeToVertex    = scala.collection.mutable.HashMap.empty[Long, Int]
    val triangleIndices = scala.collection.mutable.ArrayBuffer.empty[Int]

    @inline def gvi(ix: Int, iy: Int, iz: Int): Int = ix * ny * nz + iy * nz + iz
    @inline def edgeKey(a: Int, b: Int): Long =
      math.min(a,b).toLong * totalGridVerts + math.max(a,b).toLong

    def getOrCreateVertex(
                           ia: Int, ib: Int,
                           iax: Int, iay: Int, iaz: Int,
                           ibx: Int, iby: Int, ibz: Int
                         ): Int = {
      val key = edgeKey(ia, ib)
      edgeToVertex.getOrElse(key, {
        val va = samples(iax)(iay)(iaz)
        val vb = samples(ibx)(iby)(ibz)
        val t  = va / (va - vb)
        val vid = vertexPositions.length / 3
        vertexPositions += bounds.min.x + (iax + t * (ibx - iax)) * dx
        vertexPositions += bounds.min.y + (iay + t * (iby - iay)) * dy
        vertexPositions += bounds.min.z + (iaz + t * (ibz - iaz)) * dz
        edgeToVertex(key) = vid
        vid
      })
    }

    // Step 2 & 3: march over every cell
    for (ix <- 0 until nx-1; iy <- 0 until ny-1; iz <- 0 until nz-1) {

      // 8-bit case index: bit i set if corner i is inside (value < 0)
      var caseIdx = 0
      for (c <- 0 until 8) {
        val (cx, cy, cz) = CORNER_OFFSETS(c)
        if (samples(ix+cx)(iy+cy)(iz+cz) < 0f) caseIdx |= (1 << c)
      }

      if (caseIdx != 0 && caseIdx != 255) {
        val edgeMask = EDGE_TABLE(caseIdx)

        // Lazily compute the up-to-12 edge vertex IDs for this cell
        val edgeVids = new Array[Int](12)
        for (e <- 0 until 12) {
          if ((edgeMask & (1 << e)) != 0) {
            val (ca, cb) = EDGE_CORNERS(e)
            val (cax,cay,caz) = CORNER_OFFSETS(ca)
            val (cbx,cby,cbz) = CORNER_OFFSETS(cb)
            edgeVids(e) = getOrCreateVertex(
              gvi(ix+cax, iy+cay, iz+caz), gvi(ix+cbx, iy+cby, iz+cbz),
              ix+cax, iy+cay, iz+caz,
              ix+cbx, iy+cby, iz+cbz
            )
          }
        }

        // Emit triangles from lookup table
        val tris = TRIANGLE_TABLE(caseIdx)
        var i = 0
        while (i < tris.length && tris(i) != -1) {
          triangleIndices += edgeVids(tris(i))
          triangleIndices += edgeVids(tris(i+1))
          triangleIndices += edgeVids(tris(i+2))
          i += 3
        }
      }
    }

    Mesh(
      vertices = vertexPositions.toList,
      indices  = triangleIndices.toList,
    )
  }

  def computeSdfNormals(mesh: Mesh, sdf: Vector3f => Float, eps: Float = 1e-4f): Array[Float] = {
    val normals = new Array[Float](mesh.vertices.length)
    for (i <- mesh.vertices.indices) {
      val px = mesh.vertices(i*3); val py = mesh.vertices(i*3+1); val pz = mesh.vertices(i*3+2)
      val gx = sdf(new Vector3f(px+eps,py,pz)) - sdf(new Vector3f(px-eps,py,pz))
      val gy = sdf(new Vector3f(px,py+eps,pz)) - sdf(new Vector3f(px,py-eps,pz))
      val gz = sdf(new Vector3f(px,py,pz+eps)) - sdf(new Vector3f(px,py,pz-eps))
      val len = math.sqrt(gx*gx + gy*gy + gz*gz).toFloat.max(1e-9f)
      normals(i*3) = gx/len; normals(i*3+1) = gy/len; normals(i*3+2) = gz/len
    }
    normals
  }

  def computeFlatNormals(mesh: Mesh): Array[Float] = {
    val normals = new Array[Float](mesh.indices.length)
    val v = mesh.vertices; val idx = mesh.indices
    for (t <- 0 until mesh.indices.length/3) {
      val i0 = idx(t*3)*3; val i1 = idx(t*3+1)*3; val i2 = idx(t*3+2)*3
      val ax = v(i1)-v(i0); val ay = v(i1+1)-v(i0+1); val az = v(i1+2)-v(i0+2)
      val bx = v(i2)-v(i0); val by = v(i2+1)-v(i0+1); val bz = v(i2+2)-v(i0+2)
      val nx = ay*bz-az*by; val ny = az*bx-ax*bz; val nz = ax*by-ay*bx
      val len = math.sqrt(nx*nx+ny*ny+nz*nz).toFloat.max(1e-9f)
      for (c <- 0 until 3) {
        normals(t*9+c*3) = nx/len; normals(t*9+c*3+1) = ny/len; normals(t*9+c*3+2) = nz/len
      }
    }
    normals
  }

  def sdfToMesh2(
                  sdf:SDF,
                  bounds:Bounds,
                  resolution:Vector3i,
                  isoValue:Float = 0f,
                  smoothNormals:Boolean = false,
                  roundIterationSteps:Int = 10,
                ): Mesh2[VertexWithNormal] = {

    val mesh = this.sdfToMesh(
      sdf = sdf,
      bounds = bounds,
      resolution = resolution.x,
      isoValue = isoValue,
    )

    val triangles = for(i <- 0 until mesh.indices.length / 3) yield {
      val index = i * 3
      (
        VertexWithNormal(position = Vector3f(), normal = Vector3f()),
        VertexWithNormal(position = Vector3f(), normal = Vector3f()),
        VertexWithNormal(position = Vector3f(), normal = Vector3f()),
      )
    }

    Mesh2[VertexWithNormal](triangles.toList)
  }
}