package com.systemvi.ray_marching.test

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.ray_marching.keyboard.KeyboardToSDF
import com.systemvi.ray_marching.sdf.Union
import com.systemvi.ray_marching.sdf.mesh.{Bounds, MarchingCubes, StlExporter, SurfaceNets}
import org.joml.*

import scala.sys.process.*

object KeyboardToSdfToStlTest extends IOApp.Simple {

  override def run: IO[Unit] = {
    val openInBlender = "blender --python-expr \"import bpy; bpy.ops.wm.stl_import(filepath='./test.stl')\""

    for{
      keyboard <- IO{ TestKeyboards.keyboard2x3 }
      sdfParts <- IO{ KeyboardToSDF().toSDFParts(keyboard) }
      sdf = Union(List(
        sdfParts(0).translate(Vector3f(0,0,-20)),
        sdfParts(1).translate(Vector3f(0,0,0)),
        sdfParts(2).translate(Vector3f(0,0,20)),
      ))
      mesherStart <- IO.monotonic

      mesh <- SurfaceNets.sdfToMesh2(
        sdf = sdf,
        bounds = Bounds(min = Vector3f(-60f), max = Vector3f(60f)),
        resolution = Vector3i(100),
        isoValue = 0f,
        smoothNormals = false,
        roundIterationSteps = 10,
      )

      exporterStart <- IO.monotonic
      _ <- IO.println(s"mesh generation: ${(exporterStart-mesherStart).toSeconds}")

      _ <- IO{ StlExporter().exportToFile2(mesh, "test.stl")}

      blenderStart <- IO.monotonic
      _ <- IO.println(s"exporter: ${(blenderStart-exporterStart).toSeconds}")
      _ <- IO.blocking{ openInBlender.! }
    } yield ()
  }
}
