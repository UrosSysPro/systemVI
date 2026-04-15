package com.systemvi.ray_marching.test

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.ray_marching.keyboard.KeyboardToSDF
import com.systemvi.ray_marching.sdf.mesh.{Bounds, MarchingCubes, StlExporter, SurfaceNets}
import org.joml.*
import scala.sys.process.*

object KeyboardToSdfToStlTest extends IOApp.Simple {

  override def run: IO[Unit] = {
    val openInBlender = "blender --python-expr \"import bpy; bpy.ops.wm.stl_import(filepath='./test.stl')\""

    for{
      keyboard <- IO{ TestKeyboards.keyboard2x3 }
      sdf <- IO{ KeyboardToSDF().toSDF(keyboard) }
      mesh <- SurfaceNets.sdfToMesh2(
        sdf = sdf,
        bounds = Bounds(min = Vector3f(-50f), max = Vector3f(50f)),
        resolution = Vector3i(50),
        isoValue = 0f,
        smoothNormals = false,
        roundIterationSteps = 10,
      )
      _ <- IO{ StlExporter().exportToFile2(mesh, "test.stl")}
      _ <- IO.blocking{ openInBlender.! }
    } yield ()
  }
}
