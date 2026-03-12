package com.systemvi.ray_marching.sdf.mesh

import java.io.{DataOutputStream, FileOutputStream, BufferedOutputStream}
import java.nio.{ByteBuffer, ByteOrder}

class StlExporter(stride: Int = 3) {

  private case class Vec3(x: Float, y: Float, z: Float) {
    def -(other: Vec3): Vec3 = Vec3(x - other.x, y - other.y, z - other.z)
    def cross(other: Vec3): Vec3 = Vec3(
      y * other.z - z * other.y,
      z * other.x - x * other.z,
      x * other.y - y * other.x
    )
    def normalized: Vec3 = {
      val len = math.sqrt(x * x + y * y + z * z).toFloat
      if (len == 0f) Vec3(0f, 0f, 0f) else Vec3(x / len, y / len, z / len)
    }
  }

  private def vertex(vertices: Seq[Float], index: Int): Vec3 = {
    val base = index * stride
    Vec3(vertices(base), vertices(base + 1), vertices(base + 2))
  }

  /**
   * Write a binary STL file.
   *
   * @param vertices  Flat list of floats: x, y, z (repeated, interleaved with optional extras)
   * @param elements  Flat list of ints: triangle vertex indices (length must be divisible by 3)
   * @param fileName  Output file path (e.g. "model.stl")
   */
  def exportToFile(vertices: Seq[Float], elements: Seq[Int], fileName: String): Unit = {
    require(elements.length % 3 == 0, "Element buffer length must be a multiple of 3")
    require(vertices.length % stride == 0, s"Vertex buffer length must be a multiple of stride ($stride)")

    val triangleCount = elements.length / 3

    val out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))

    try {
      // --- 80-byte ASCII header (content is ignored by most parsers) ---
      val header = "Binary STL exported by StlExporter".padTo(80, ' ').take(80)
      out.write(header.getBytes("ASCII"))

      // --- Triangle count (uint32, little-endian) ---
      out.write(intToLeBytes(triangleCount))

      // --- One record per triangle: normal (3×f32) + 3 vertices (3×3×f32) + attribute (uint16) ---
      val buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)

      def writeFloat(f: Float): Unit = {
        buf.clear()
        buf.putFloat(f)
        out.write(buf.array())
      }

      def writeVec3(v: Vec3): Unit = { writeFloat(v.x); writeFloat(v.y); writeFloat(v.z) }

      for (i <- 0 until triangleCount) {
        val v0 = vertex(vertices, elements(i * 3))
        val v1 = vertex(vertices, elements(i * 3 + 1))
        val v2 = vertex(vertices, elements(i * 3 + 2))

        val normal = (v1 - v0).cross(v2 - v0).normalized

        writeVec3(normal)   // face normal
        writeVec3(v0)       // vertex 1
        writeVec3(v1)       // vertex 2
        writeVec3(v2)       // vertex 3
        out.write(Array[Byte](0, 0)) // attribute byte count (unused)
      }

    } finally {
      out.close()
    }

    println(s"STL written: $fileName ($triangleCount triangles)")
  }

  /** Pack a 32-bit int as 4 little-endian bytes. */
  private def intToLeBytes(value: Int): Array[Byte] = {
    ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()
  }
}