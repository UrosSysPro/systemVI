package com.systemvi.voxel.world

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.texture.Texture.{FilterMag, FilterMin, Repeat}
import com.systemvi.engine.texture.{Format, FrameBuffer, Texture}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import com.systemvi.voxel.world.buffer.GBuffer
import com.systemvi.voxel.world.debug.*
import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import com.systemvi.voxel.world.renderer.{BlockFaceRenderer, Light, Projection, ShadowMapRenderer, SkyBoxRenderer}
import com.systemvi.voxel.world.world2.{Chunk, World, WorldCache}
import org.joml.{Vector2f, Vector3f, Vector3i, Vector4f}

object DemoApp extends Game(3, 3, 60, 800, 600, "Demo Game") {

  val numberOfChunks = Vector3i(10, 10, 10)

  val generator: WorldGenerator = PerlinWorldGenerator()
  val world: World = World(numberOfChunks)
  world.generate(generator)
  val worldCache: WorldCache = WorldCache(world)
  var blockRenderer: BlockFaceRenderer = null
  var skyboxRenderer: SkyBoxRenderer = null
  var controller: CameraController3 = null

  var gbuffer: GBuffer = null
  var hdrTexture: Texture = null
  var frameBuffer: FrameBuffer = null
  var skyboxTexture: Texture = null
  var skyboxFrameBuffer: FrameBuffer = null

  var diffuseMap: Texture = null
  var normalMap: Texture = null

  var viewerCamera: Camera3 = null
  var emptyVertexArray: VertexArray = null
  var positionBufferViewer: PositionViewer = null
  var uvBufferViewer: UVViewer = null
  var depthBufferViewer: DepthViewer = null
  var tbnBufferViewer: TBNViewer = null
  var toneMapper: ToneMapper = null
  var shadowMapRenderer: ShadowMapRenderer = null

  var combinedViewer: Shader = null

  val near = 0.1f
  val far = 1000f


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
    blockRenderer = BlockFaceRenderer()

    val shadowMapWidth=2000
    val shadowMapHeight=2000
    shadowMapRenderer = ShadowMapRenderer(
      width = shadowMapWidth,
      height = shadowMapHeight,
      light = Light(
        position = Vector3f(-50, 200, -50),
        rotation = Vector3f(-Math.PI.toFloat / 4f, -Math.PI.toFloat * 3f / 4f, 0),
        projection = Projection(shadowMapWidth.toFloat/shadowMapHeight.toFloat, Math.PI.toFloat / 3f, 0.1f, 1000f)
      )
    )
    skyboxRenderer = SkyBoxRenderer()
    blockRenderer.setRenderTargets(gbuffer.frameBuffer.getAttachments)
    hdrTexture = Texture.builder()
      .width(width.toInt)
      .height(height.toInt)
      .format(Format.RGB32F)
      .verticalRepeat(Repeat.CLAMP_EDGE)
      .horizontalRepeat(Repeat.CLAMP_EDGE)
      .build()
    frameBuffer = FrameBuffer.builder()
      .color(hdrTexture)
      .build()

    skyboxTexture = Texture.builder()
      .width(width.toInt)
      .height(height.toInt)
      .format(Format.RGB)
      .build()
    skyboxFrameBuffer = FrameBuffer.builder()
      .color(skyboxTexture)
      .build()

    diffuseMap = Texture.builder()
      .filterMag(FilterMag.NEAREST)
      .filterMin(FilterMin.NEAREST_MIPMAP_LINEAR)
      .file("assets/examples/minecraft/textures/diffuse.png")
      .build()
    normalMap = Texture.builder()
      .filterMag(FilterMag.NEAREST)
      .filterMin(FilterMin.NEAREST_MIPMAP_LINEAR)
      .file("assets/examples/minecraft/textures/normal.png")
      .build()

    for {
      col0 <- worldCache.chunkCache
      col1 <- col0
      chunkCache <- col1
    } {
      blockRenderer.draw(chunkCache.blockFaces)
      shadowMapRenderer.draw(chunkCache.blockFaces)
    }
    blockRenderer.upload()
    shadowMapRenderer.upload()
    shadowMapRenderer.drawUploaded()

    viewerCamera = Camera3.builder2d()
      .position(width / 2, height / 2)
      .size(width, height)
      .scale(1, 1)
      .build()

    positionBufferViewer = PositionViewer(Vector3i(numberOfChunks).mul(Chunk.size))
    uvBufferViewer = UVViewer()
    depthBufferViewer = DepthViewer()
    tbnBufferViewer = TBNViewer()

    toneMapper = ToneMapper()

    combinedViewer = Shader.builder()
      .fragment("assets/examples/voxels/combined/fragment.glsl")
      .vertex("assets/examples/voxels/combined/vertex.glsl")
      .build()

    emptyVertexArray = VertexArray()

    //    window.setPosition(100,100)
    //    window.setSize(1600,1200)
  }

  override def loop(delta: Float): Unit = {
    val width = getWindow.getWidth.toFloat
    val height = getWindow.getHeight.toFloat
    controller.update(delta)

    Utils.clear(Colors.green500, Buffer.COLOR_BUFFER)

    gbuffer.begin()
    Utils.clear(Colors.black, Buffer.COLOR_BUFFER, Buffer.DEPTH_BUFFER)
    Utils.enableDepthTest()
    Utils.enableFaceCulling()
    blockRenderer.time = Utils.getTime.toFloat
    blockRenderer.view(controller.camera.view)
    blockRenderer.projection(controller.camera.projection)
    blockRenderer.drawUploaded()
    Utils.disableFaceCulling()
    Utils.disableDepthTest()
    gbuffer.end()

    positionBufferViewer.draw(
      gbuffer.position,
      viewerCamera.view,
      viewerCamera.projection,
      Vector4f(0, 0, width / 2, height / 2)
    )
    uvBufferViewer.draw(
      gbuffer.uv,
      diffuseMap,
      viewerCamera.view,
      viewerCamera.projection,
      Vector4f(width / 2, 0, width / 2, height / 2)
    )
    depthBufferViewer.draw(
      //      gbuffer.depth,
      depthBuffer=shadowMapRenderer.shadowMap,
      near=shadowMapRenderer.light.projection.near,
      far=shadowMapRenderer.light.projection.far,
      viewerCamera.view,
      viewerCamera.projection,
      Vector4f(0, height / 2, width / 2, height / 2)
    )
    tbnBufferViewer.draw(
      texture = gbuffer.occlusion,
      view = viewerCamera.view,
      projection = viewerCamera.projection,
      rect = Vector4f(width / 2, height / 2, width / 2, height / 2)
    )

    skyboxFrameBuffer.begin()
    skyboxRenderer.view(controller.camera.view)
    skyboxRenderer.projection(controller.camera.projection)
    skyboxRenderer.position(controller.camera.position)
    skyboxRenderer.draw()
    skyboxFrameBuffer.end()

    frameBuffer.begin()
    combinedViewer.use()
    gbuffer.bind()
    diffuseMap.bind(6)
    normalMap.bind(7)
    skyboxTexture.bind(8)
    shadowMapRenderer.shadowMap.bind(9)
    combinedViewer.setUniform("view", viewerCamera.view)
    combinedViewer.setUniform("projection", viewerCamera.projection)
    combinedViewer.setUniform("positionBuffer", 0)
    combinedViewer.setUniform("normalBuffer", 1)
    combinedViewer.setUniform("tangentBuffer", 2)
    combinedViewer.setUniform("uvBuffer", 3)
    combinedViewer.setUniform("occlusionBuffer", 4)
    combinedViewer.setUniform("depthBuffer", 5)
    combinedViewer.setUniform("diffuseMap", 6)
    combinedViewer.setUniform("normalMap", 7)
    combinedViewer.setUniform("skybox", 8)
    combinedViewer.setUniform("shadowMap", 9)
    combinedViewer.setUniform("camera.position", controller.camera.position)
    combinedViewer.setUniform("camera.view", controller.camera.view)
    combinedViewer.setUniform("camera.projection", controller.camera.projection)
    combinedViewer.setUniform("camera.far", far)
    combinedViewer.setUniform("camera.near", near)
    combinedViewer.setUniform("rect", Vector4f(0, 0, width, height))
    combinedViewer.setUniform("far", far)
    combinedViewer.setUniform("near", near)
    combinedViewer.setUniform("shadowMapInfo.position",shadowMapRenderer.light.position)
    combinedViewer.setUniform("shadowMapInfo.rotation",shadowMapRenderer.light.rotation)
    combinedViewer.setUniform("shadowMapInfo.view",shadowMapRenderer.getView)
    combinedViewer.setUniform("shadowMapInfo.projection",shadowMapRenderer.getProjection)
    combinedViewer.setUniform("shadowMapInfo.fov",shadowMapRenderer.light.projection.fov)
    combinedViewer.setUniform("shadowMapInfo.aspect",shadowMapRenderer.light.projection.aspect)
    combinedViewer.setUniform("shadowMapInfo.near",shadowMapRenderer.light.projection.near)
    combinedViewer.setUniform("shadowMapInfo.far",shadowMapRenderer.light.projection.far)
    combinedViewer.setUniform("shadowMapInfo.bias",0.0001f)
    combinedViewer.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
    frameBuffer.end()

        toneMapper.draw(
          texture = hdrTexture,
          size = Vector2f(width, height),
          view = viewerCamera.view,
          projection = viewerCamera.projection,
//          rect = Vector4f(width /2, height / 4, width / 2, height / 2)
          rect = Vector4f(0,0,width,height)
        )
  }
}
