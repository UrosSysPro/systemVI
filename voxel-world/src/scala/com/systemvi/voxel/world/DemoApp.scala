package com.systemvi.voxel.world

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.texture.Texture.{FilterMag, FilterMin, Repeat}
import com.systemvi.engine.texture.{Format, FrameBuffer, Texture}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import com.systemvi.voxel.world.buffer.GBuffer
import com.systemvi.voxel.world.debug.*
import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import com.systemvi.voxel.world.renderer.*
import com.systemvi.voxel.world.world2.{Chunk, World, WorldCache}
import org.joml.{Vector2f, Vector3f, Vector3i, Vector4f}

object DemoApp extends Game(3, 3, 60, 1400, 900, "Demo Game") {

  val numberOfChunks = Vector3i(2, 2, 2)

  val generator: WorldGenerator = PerlinWorldGenerator()
  val world: World = World(numberOfChunks)
  world.generate(generator)
  val worldCache: WorldCache = WorldCache(world)
  var blockRenderer: BlockFaceRenderer = null
  var skyboxRenderer: SkyBoxRenderer = null
  var phongDeferredRenderer: PhongDeferredRenderer = null
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
  var aoViewer:AOViewer=null
  var toneMapper: ToneMapper = null
  var shadowMapRenderer: ShadowMapRenderer = null

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
      .speed(5)
      .build()
    setInputProcessor(controller)

    gbuffer = GBuffer(width.toInt, height.toInt)
    blockRenderer = BlockFaceRenderer()

    val shadowMapWidth = 4000
    val shadowMapHeight = 4000
    shadowMapRenderer = ShadowMapRenderer(
      width = shadowMapWidth,
      height = shadowMapHeight,
      light = Light(
        color = Vector3f(1000),
        attenuation = Vector3f(0.5f, 0.5, 1.0f),
        position = Vector3f(-10, 60, -10),
        rotation = Vector3f(-Math.PI.toFloat / 4f, -Math.PI.toFloat * 3f / 4f, 0),
        projection = Projection(shadowMapWidth.toFloat / shadowMapHeight.toFloat, Math.PI.toFloat / 3f, 0.1f, 100f)
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

    Utils.viewport(0, 0, shadowMapRenderer.width, shadowMapRenderer.height)
    shadowMapRenderer.upload()
    shadowMapRenderer.drawUploaded()

    Utils.viewport(0, 0, width.toInt, height.toInt)
    blockRenderer.upload()

    viewerCamera = Camera3.builder2d()
      .position(width / 2, height / 2)
      .size(width, height)
      .scale(1, 1)
      .build()

    positionBufferViewer = PositionViewer(Vector3i(numberOfChunks).mul(Chunk.size))
    uvBufferViewer = UVViewer()
    depthBufferViewer = DepthViewer()
    tbnBufferViewer = TBNViewer()
    aoViewer=AOViewer()

    toneMapper = ToneMapper()

    phongDeferredRenderer=PhongDeferredRenderer(
      gbuffer = gbuffer,
      diffuseMap = diffuseMap,
      normalMap = normalMap,
      skyboxTexture = skyboxTexture,
      shadowMap = shadowMapRenderer.shadowMap,
      viewerCamera = viewerCamera,
      worldCamera = controller.camera,
      shadowMapLight = ShadowMapLight(
        position = shadowMapRenderer.light.position,
        rotation = shadowMapRenderer.light.rotation,
        view = shadowMapRenderer.getView,
        projection = shadowMapRenderer.getProjection,
        fov = shadowMapRenderer.light.projection.fov,
        near = shadowMapRenderer.light.projection.near,
        aspect = shadowMapRenderer.light.projection.aspect,
        far = shadowMapRenderer.light.projection.far,
        bias = 0.00002f,
        attenuation = Vector3f(0.5f,0.5f,1.0f),
        color = shadowMapRenderer.light.color
      )
    )

    emptyVertexArray = VertexArray()
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
      depthBuffer = shadowMapRenderer.shadowMap,
      near = shadowMapRenderer.light.projection.near,
      far = shadowMapRenderer.light.projection.far,
      viewerCamera.view,
      viewerCamera.projection,
      Vector4f(0, height / 2, height / 2, height / 2)
    )
    aoViewer.draw(
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
    phongDeferredRenderer.draw(Vector4f(0,0,width,height))
    frameBuffer.end()

    toneMapper.draw(
      texture = hdrTexture,
      size = Vector2f(width, height),
      view = viewerCamera.view,
      projection = viewerCamera.projection,
      rect = Vector4f(width / 2, height / 4, width / 2, height / 2)
      //          rect = Vector4f(0,0,width,height)
    )
  }
}
