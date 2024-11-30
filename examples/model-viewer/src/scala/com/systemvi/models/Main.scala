package com.systemvi.models

import cats.effect.{IO, IOApp}
import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.camera.CameraController3
import com.systemvi.engine.model.{Model, ModelLoaderParams, ModelUtils, VertexAttribute}
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.Matrix4f

import scala.concurrent.duration.*


object Main extends Game(3, 3, 60, 800, 600, "Model Viewer") {

  // ucitati niz vertexa
  // poslati 3d model na gpu
  // postaviti kameru
  // postaviti shader

  case class MeshGpuData(vertexArray: VertexArray, arrayBuffer: ArrayBuffer, elementBuffer: ElementsBuffer)

  var gpuData: List[MeshGpuData] = null

  var controller: CameraController3 = null

  var shader: Shader = null

  var model: Model = null

  override def setup(window: Window): Unit = {

    controller = CameraController3.builder()
      .window(window)
      .aspect(window.getWidth.toFloat/window.getHeight.toFloat)
      .build()
    setInputProcessor(controller)

    model = ModelUtils.load(ModelLoaderParams.builder()
      .fileName("assets/glb/vehicle-speedster.glb")
      .triangulate()
      .calcTangentSpace()
      .genSmoothNormals()
      .fixInfacingNormals()
      .genSmoothNormals()
      .flipUVs()
      .joinIdenticalVertices()
      .build())

    gpuData = model.meshes.toArray.map({
      case mesh: Model.Mesh =>
        val vertexArray = VertexArray()
        val arrayBuffer = ArrayBuffer()
        val elementsBuffer = ElementsBuffer()

        vertexArray.bind()
        arrayBuffer.bind()
        elementsBuffer.bind()
        arrayBuffer.setVertexAttributes(Array(
          //          VertexAttribute("color",4),
          //          VertexAttribute("normal",3),
          //          VertexAttribute("tangent",3),
          //          VertexAttribute("bitangent",3),
          VertexAttribute("position", 3),
          //          VertexAttribute("texCoords",3),
        ))

        arrayBuffer.setData(mesh.vertices.toArray.flatMap {
          case vertex: Model.Vertex =>
            val p = vertex.position
            Array(p.x, p.y, p.z)
        })

        elementsBuffer.setData(mesh.faces.toArray.flatMap {
          case face: Model.Face => face.indices
        })

        MeshGpuData(vertexArray, arrayBuffer, elementsBuffer)
    }).toList

    shader = Shader.builder()
      .vertex("vertex.glsl")
      .fragment("fragment.glsl")
      .build()
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.black, Buffer.COLOR_BUFFER, Buffer.DEPTH_BUFFER)
    Utils.enableDepthTest()
    controller.update(delta)

    shader.use()
    shader.setUniform("view", controller.camera.view)
    shader.setUniform("projection", controller.camera.projection)

    def drawNode(node: Model.Node, transform: Matrix4f): Unit = if (node != null) {
      val t = Matrix4f(transform).mul(node.transform)

      node.meshIndices.toArray().foreach {
        case index: Int =>
          val data=gpuData(index)
          data.vertexArray.bind()
          shader.setUniform("model",t)
          shader.drawElements(
            Primitive.TRIANGLES,
            model.meshes.get(index).faces.size(),
            ElementsDataType.UNSIGNED_INT,
            3
          )
      }

      node.children.toArray().foreach{
        case node:Model.Node=>
          drawNode(node,t)
      }
    }

    drawNode(model.root,Matrix4f())
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}
