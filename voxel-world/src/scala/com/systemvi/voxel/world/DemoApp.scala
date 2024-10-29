package com.systemvi.voxel.world

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.texture.Texture
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import com.systemvi.voxel.world.buffer.GBuffer
import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import com.systemvi.voxel.world.renderer.BlockFaceRenderer
import com.systemvi.voxel.world.world2.{Chunk, World, WorldCache}
import org.joml.{Vector3i, Vector4f}
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.{GL_NEAREST, GL_NEAREST_MIPMAP_LINEAR}

object DemoApp extends Game(3, 3, 60, 1200, 500, "Demo Game") {


  val numberOfChunks = Vector3i(2, 1, 2)

  val generator: WorldGenerator = PerlinWorldGenerator()
  val world: World = World(numberOfChunks)
  world.generate(generator)
  val worldCache: WorldCache = WorldCache(world)
  val blockRenderer: BlockFaceRenderer = BlockFaceRenderer()
  var controller: CameraController3 = null

  var gbuffer: GBuffer = null

  var diffuseMap: Texture = null
  var normalMap: Texture = null

  var viewerCamera: Camera3 = null
  var emptyVertexArray: VertexArray = null
  var positionBufferViewer: Shader = null
  var uvBufferViewer: Shader = null
  var depthBufferViewer: Shader = null
  var tbnBufferViewer: Shader = null

  var combinedViewer: Shader = null

  val near = 0.1f
  val far = 100f


  override def setup(window: Window): Unit = {
    val width = window.getWidth.toFloat
    val height = window.getHeight.toFloat

    controller = CameraController3.builder()
      .window(window)
      .aspect(width / height)
      .far(far)
      .near(near)
      .speed(20)
      .build()
    setInputProcessor(controller)
    gbuffer = GBuffer(width.toInt, height.toInt)
    diffuseMap = Texture("assets/examples/minecraft/textures/diffuse.png")
    normalMap = Texture("assets/examples/minecraft/textures/normal.png")
    diffuseMap.setSamplerFilter(GL_NEAREST_MIPMAP_LINEAR, GL_NEAREST)
    normalMap.setSamplerFilter(GL_NEAREST_MIPMAP_LINEAR, GL_NEAREST)
    for {
      col0 <- worldCache.chunkCache
      col1 <- col0
      chunkCache <- col1
    } blockRenderer.draw(chunkCache.blockFaces)
    blockRenderer.upload()


    viewerCamera = Camera3.builder2d()
      .position(width / 2, height / 2)
      .size(width, height)
      .build()

    positionBufferViewer = Shader.builder()
      .fragment("assets/examples/voxels/positionBufferViewer/fragment.glsl")
      .vertex("assets/examples/voxels/positionBufferViewer/vertex.glsl")
      .build()
    uvBufferViewer = Shader.builder()
      .fragment("assets/examples/voxels/uvBufferViewer/fragment.glsl")
      .vertex("assets/examples/voxels/uvBufferViewer/vertex.glsl")
      .build()
    depthBufferViewer = Shader.builder()
      .fragment("assets/examples/voxels/depthBufferViewer/fragment.glsl")
      .vertex("assets/examples/voxels/depthBufferViewer/vertex.glsl")
      .build()
    tbnBufferViewer = Shader.builder()
      .fragment("assets/examples/voxels/tbnBufferViewer/fragment.glsl")
      .vertex("assets/examples/voxels/tbnBufferViewer/vertex.glsl")
      .build()
    combinedViewer = Shader.builder()
      .fragment("assets/examples/voxels/combined/fragment.glsl")
      .vertex("assets/examples/voxels/combined/vertex.glsl")
      .build()
    emptyVertexArray = VertexArray()
  }

  override def loop(delta: Float): Unit = {
    val width = getWindow.getWidth.toFloat
    val height = getWindow.getHeight.toFloat
    controller.update(delta)

    gbuffer.begin()
    Utils.clear(Colors.black, Buffer.COLOR_BUFFER, Buffer.DEPTH_BUFFER)

    Utils.enableDepthTest()
    Utils.enableFaceCulling()
    blockRenderer.time = GLFW.glfwGetTime().toFloat
    blockRenderer.view(controller.camera.view)
    blockRenderer.projection(controller.camera.projection)
    blockRenderer.drawUploaded()
    Utils.disableFaceCulling()
    Utils.disableDepthTest()
    gbuffer.end()

    Utils.clear(Colors.green500, Buffer.COLOR_BUFFER)

    emptyVertexArray.bind()
    positionBufferViewer.use()
    gbuffer.position.bind(0)
    positionBufferViewer.setUniform("view", viewerCamera.view)
    positionBufferViewer.setUniform("projection", viewerCamera.projection)
    positionBufferViewer.setUniform("worldSize", new Vector3i(numberOfChunks).mul(Chunk.size))
    positionBufferViewer.setUniform("positionBuffer", 0)
    positionBufferViewer.setUniform("rect", Vector4f(0, 0, width / 2, height / 2))
    positionBufferViewer.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)

    uvBufferViewer.use()
    gbuffer.uv.bind(0)
    diffuseMap.bind(1)
    uvBufferViewer.setUniform("view", viewerCamera.view)
    uvBufferViewer.setUniform("projection", viewerCamera.projection)
    uvBufferViewer.setUniform("uvBuffer", 0)
    uvBufferViewer.setUniform("textureBuff", 1)
    uvBufferViewer.setUniform("rect", Vector4f(width / 2, 0, width / 2, height / 2))
    uvBufferViewer.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)

    depthBufferViewer.use()
    gbuffer.depth.bind(0)
    depthBufferViewer.setUniform("view", viewerCamera.view)
    depthBufferViewer.setUniform("projection", viewerCamera.projection)
    depthBufferViewer.setUniform("near", near)
    depthBufferViewer.setUniform("far", far)
    depthBufferViewer.setUniform("depthBuffer", 0)
    depthBufferViewer.setUniform("rect", Vector4f(0, height / 2, width / 2, height / 2))
    depthBufferViewer.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)

    tbnBufferViewer.use()
    gbuffer.normal.bind(0)
    tbnBufferViewer.setUniform("view", viewerCamera.view)
    tbnBufferViewer.setUniform("projection", viewerCamera.projection)
    tbnBufferViewer.setUniform("textureBuff", 0)
    tbnBufferViewer.setUniform("rect", Vector4f(width / 2, height / 2, width / 2, height / 2))
    tbnBufferViewer.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)

    combinedViewer.use()
    gbuffer.bind()
    diffuseMap.bind(4)
    normalMap.bind(5)
    combinedViewer.setUniform("view", viewerCamera.view)
    combinedViewer.setUniform("projection", viewerCamera.projection)
    combinedViewer.setUniform("positionBuffer", 0)
    combinedViewer.setUniform("normalBuffer", 1)
    combinedViewer.setUniform("uvBuffer", 2)
    combinedViewer.setUniform("depthBuffer", 3)
    combinedViewer.setUniform("diffuseMap", 4)
    combinedViewer.setUniform("normalMap", 5)
    combinedViewer.setUniform("camera.position", controller.camera.position)
    combinedViewer.setUniform("rect", Vector4f(width / 4, height / 4, width / 2, height / 2))
    combinedViewer.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}
