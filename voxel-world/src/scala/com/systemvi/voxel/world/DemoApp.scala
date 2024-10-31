package com.systemvi.voxel.world

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.texture.Texture
import com.systemvi.engine.texture.Texture.{FilterMag, FilterMin}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import com.systemvi.voxel.world.buffer.GBuffer
import com.systemvi.voxel.world.debug.{DepthViewer, PositionViewer, TBNViewer, UVViewer}
import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import com.systemvi.voxel.world.renderer.BlockFaceRenderer
import com.systemvi.voxel.world.world2.{Chunk, World, WorldCache}
import org.joml.{Vector3i, Vector4f}
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.{GL_NEAREST, GL_NEAREST_MIPMAP_LINEAR}

object DemoApp extends Game(3, 3, 60, 800, 600, "Demo Game") {

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
  var positionBufferViewer: PositionViewer = null
  var uvBufferViewer: UVViewer = null
  var depthBufferViewer: DepthViewer = null
  var tbnBufferViewer: TBNViewer = null

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
    diffuseMap.setSamplerFilter(FilterMin.NEAREST_MIPMAP_LINEAR,FilterMag.NEAREST)
    normalMap.setSamplerFilter(FilterMin.NEAREST_MIPMAP_LINEAR,FilterMag.NEAREST)
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

    positionBufferViewer=PositionViewer(Vector3i(numberOfChunks).mul(Chunk.size))
    uvBufferViewer=UVViewer()
    depthBufferViewer=DepthViewer()
    
    tbnBufferViewer = TBNViewer()
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

    positionBufferViewer.draw(gbuffer.position,viewerCamera.view,viewerCamera.projection,Vector4f(0,0,width/2,height/2))
    uvBufferViewer.draw(gbuffer.uv,diffuseMap,viewerCamera.view,viewerCamera.projection,Vector4f(width/2,0,width/2,height/2))
    depthBufferViewer.draw(gbuffer.depth,near,far,viewerCamera.view,viewerCamera.projection, Vector4f(0, height / 2, width / 2, height / 2))
    tbnBufferViewer.draw(gbuffer.normal, view = viewerCamera.view, projection = viewerCamera.projection, rect = Vector4f(width/2,height/2,width/2,height/2))

    combinedViewer.use()
    gbuffer.bind()
    diffuseMap.bind(5)
    normalMap.bind(6)
    combinedViewer.setUniform("view", viewerCamera.view)
    combinedViewer.setUniform("projection", viewerCamera.projection)
    combinedViewer.setUniform("positionBuffer", 0)
    combinedViewer.setUniform("normalBuffer", 1)
    combinedViewer.setUniform("uvBuffer", 2)
    combinedViewer.setUniform("occlusionBuffer",3)
    combinedViewer.setUniform("depthBuffer", 4)
    combinedViewer.setUniform("diffuseMap", 5)
    combinedViewer.setUniform("normalMap", 6)
    combinedViewer.setUniform("camera.position", controller.camera.position)
    combinedViewer.setUniform("rect", Vector4f(width / 4, height / 4, width / 2, height / 2))
    combinedViewer.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}
