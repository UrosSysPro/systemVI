package com.systemvi.voxel.world

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.camera.{Camera3, CameraController3, ProjectionType}
import com.systemvi.engine.texture.Texture.{FilterMag, FilterMin, Repeat}
import com.systemvi.engine.texture.{Format, FrameBuffer, Texture}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.{InputMultiplexer, Window}
import com.systemvi.voxel.world.buffer.GBuffer
import com.systemvi.voxel.world.debug.*
import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import com.systemvi.voxel.world.renderer.*
import com.systemvi.voxel.world.world.{Block, Chunk, World, WorldCache}
import org.joml.{Vector2f, Vector3f, Vector3i, Vector4f}
import org.lwjgl.glfw.GLFW

case class InitialLightParams(position: Vector3f, rotation: Vector3f,near:Float=0.1,far:Float=100,attenuation:Vector3f=Vector3f(0.1f, 0.1, 1.0f))

case class DemoAppConfig(
                          generator: WorldGenerator = PerlinWorldGenerator(),
                          numberOfChunks: Vector3i = Vector3i(2, 2, 2),
                          initialLightParams: InitialLightParams = InitialLightParams(
                            position = Vector3f(-10, 60, -10),
                            rotation = Vector3f(-Math.PI.toFloat / 4f, -Math.PI.toFloat * 3f / 4f, 0)
                          ),
                          initialPlayerPosition:Vector3f=Vector3f(0,20,0),
                        )

class DemoApp(config: DemoAppConfig) extends Game(3, 3, 60, 1400, 900, "Demo Game") {

  val generator: WorldGenerator = config.generator
  val numberOfChunks: Vector3i = config.numberOfChunks

  val world: World = World(numberOfChunks)
  world.generate(generator)
//  generateFractal()
  val worldCache: WorldCache = WorldCache(world)
  var blockRenderer: BlockFaceRenderer = null
  var skyboxRenderer: SkyBoxRenderer = null
  var phongDeferredRenderer: PhongDeferredRenderer = null
  var pbrDeferredRenderer: PBRDeferredRenderer = null
  var controller: CameraController3 = null
  var shadowController: CameraController3 = null

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
  var aoViewer: AOViewer = null
  var toneMapper: ToneMapper = null
  var shadowMapRenderer: ShadowMapRenderer = null

  val near = 0.1f
  val far = 1000f
  val shadowMapWidth = 4000
  val shadowMapHeight = 4000

  val currentRenderView=0
  val renderViews=List[()=>Unit](renderView1,renderView2)

  private def cameraSetup():Unit={
    val window=getWindow
    val width=window.getWidth.toFloat
    val height=window.getHeight.toFloat
    val p=config.initialPlayerPosition
    controller = CameraController3.builder()
      .window(getWindow)
      .camera(Camera3.builder3d()
        .position(p.x,p.y,p.z)
        .build()
      )
      .aspect(width / height)
      .far(far)
      .near(near)
      .speed(5)
      .build()
  }

  private def shadowMapCameraSetup():Unit={
    val p = config.initialLightParams.position
    val r = config.initialLightParams.rotation
    val window=getWindow
    val width=window.getWidth.toFloat
    val height=window.getHeight.toFloat
    shadowController = CameraController3.builder()
      .camera(Camera3.builder3d()
        .position(p.x, p.y, p.z)
        .rotation(r.x, r.y, r.z)
        .aspect(shadowMapWidth.toFloat / shadowMapHeight.toFloat)
        .fov(Math.PI.toFloat / 3f)
        .near(0.1f)
        .far(2000.0f)
        .build())
      .window(window)
      .aspect(width / height)
      .far(config.initialLightParams.far)
      .near(config.initialLightParams.near)
      .speed(5)
      .build()
    shadowMapRenderer = ShadowMapRenderer(
      width = shadowMapWidth,
      height = shadowMapHeight,
      light = Light(
        color = Vector3f(1000),
        attenuation = config.initialLightParams.attenuation,
        position = shadowController.camera.position,
        rotation = shadowController.camera.rotation,
        projection = shadowController.camera.projectionType match
          case ProjectionType.Perspective(aspect, fov, near, far) => Projection(aspect, fov, near, far)
          case _ => Projection(shadowMapWidth.toFloat / shadowMapHeight.toFloat, Math.PI.toFloat / 3f, 0.1f, 100f)
      )
    )
  }

  private def texturesSetup():Unit={
    val window=getWindow
    val width=window.getWidth.toFloat
    val height=window.getHeight.toFloat
    gbuffer = GBuffer(width.toInt, height.toInt)
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
  }

  override def setup(window: Window): Unit = {
    val width = window.getWidth.toFloat
    val height = window.getHeight.toFloat

    cameraSetup()
    shadowMapCameraSetup()

    setInputProcessor(InputMultiplexer(
      controller, this
    ))

    blockRenderer = BlockFaceRenderer()
    skyboxRenderer = SkyBoxRenderer()

    texturesSetup()

    blockRenderer.setRenderTargets(gbuffer.frameBuffer.getAttachments)

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
    //    shadowMapRenderer.drawUploaded()

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
    aoViewer = AOViewer()

    toneMapper = ToneMapper()

    //    phongDeferredRenderer=PhongDeferredRenderer(
    //      gbuffer = gbuffer,
    //      diffuseMap = diffuseMap,
    //      normalMap = normalMap,
    //      skyboxTexture = skyboxTexture,
    //      shadowMap = shadowMapRenderer.shadowMap,
    //      viewerCamera = viewerCamera,
    //      worldCamera = controller.camera,
    //      shadowMapLight = ShadowMapLight(
    //        position = shadowMapRenderer.light.position,
    //        rotation = shadowMapRenderer.light.rotation,
    //        view = shadowMapRenderer.getView,
    //        projection = shadowMapRenderer.getProjection,
    //        fov = shadowMapRenderer.light.projection.fov,
    //        near = shadowMapRenderer.light.projection.near,
    //        aspect = shadowMapRenderer.light.projection.aspect,
    //        far = shadowMapRenderer.light.projection.far,
    //        bias = 0.000002f,
    //        attenuation = Vector3f(0.5f,0.5f,1.0f),
    //        color = shadowMapRenderer.light.color
    //      )
    //    )
    pbrDeferredRenderer = PBRDeferredRenderer(
      gbuffer = gbuffer,
      diffuseMap = diffuseMap,
      normalMap = normalMap,
      skyboxTexture = skyboxTexture,
      shadowMap = shadowMapRenderer.shadowMap,
      playerCamera = controller.camera,
      viewerCamera = viewerCamera,
      shadowMapRenderer = shadowMapRenderer,
      bias = 0.000002f,
    )
    emptyVertexArray = VertexArray()
  }

  override def loop(delta: Float): Unit = {
    val width = getWindow.getWidth.toFloat
    val height = getWindow.getHeight.toFloat
    controller.update(delta)
    shadowController.update(delta)

    Utils.clear(Colors.green500, Buffer.COLOR_BUFFER)

    renderGBuffer()

    renderShadowMap()

    renderSkyBox()
    frameBuffer.begin()
    //phongDeferredRenderer.draw(Vector4f(0,0,width,height))
    pbrDeferredRenderer.draw(Vector4f(0, 0, width, height))
    frameBuffer.end()

    renderViews(currentRenderView)()
  }

  private def renderGBuffer():Unit={
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
  }

  private def renderShadowMap():Unit= {
    shadowMapRenderer.light.position.set(shadowController.camera.position)
    shadowMapRenderer.light.rotation.set(shadowController.camera.rotation)
    Utils.viewport(0, 0, shadowMapRenderer.width, shadowMapRenderer.height)
    shadowMapRenderer.drawUploaded()
    Utils.viewport(0, 0, getWindow.getWidth, getWindow.getHeight)
  }

  private def renderSkyBox():Unit={
    skyboxFrameBuffer.begin()
    skyboxRenderer.view(controller.camera.view)
    skyboxRenderer.projection(controller.camera.projection)
    skyboxRenderer.position(controller.camera.position)
    skyboxRenderer.draw()
    skyboxFrameBuffer.end()
  }

  private def renderView1():Unit={
    val window=getWindow
    val width=window.getWidth.toFloat
    val height=window.getHeight.toFloat
    toneMapper.draw(
      texture = hdrTexture,
      size = Vector2f(width, height),
      view = viewerCamera.view,
      projection = viewerCamera.projection,
      rect = Vector4f(0, 0, width, height)
    )
  }

  private def renderView2():Unit={
    val window=getWindow
    val width=window.getWidth.toFloat
    val height=window.getHeight.toFloat
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
      viewerCamera.view, viewerCamera.projection,
      Vector4f(0, height / 2, height / 2, height / 2) )
    aoViewer.draw(
      texture = gbuffer.occlusion,
      view = viewerCamera.view,
      projection = viewerCamera.projection,
      rect = Vector4f(width / 2, height / 2, width / 2, height / 2)
    )
  }

  def generateFractal(): Unit = {
    val maxSteps = 4
    for (i <- 0 until 81) {
      for (j <- 0 until 81) {
        for (k <- 0 until 81) {
          var isEmpty = false
          var size = 3
          for (l <- 1 to maxSteps) {
            val x = (i % size) / (size / 3)
            val y = (j % size) / (size / 3)
            val z = (k % size) / (size / 3)
            if ((x == 1 && y == 1) || (x == 1 && z == 1) || (z == 1 && y == 1)) isEmpty = true
            size *= 3
          }
          if (isEmpty) world.getBlockState(i, j, k).block = Block.AIR
          else world.getBlockState(i, j, k).block = Block.STONE
        }
      }
    }
  }

  override def keyDown(key: Int, scancode: Int, mods: Int): Boolean = {
    key match
      case GLFW.GLFW_KEY_1 =>
        setInputProcessor(InputMultiplexer(
          controller, this
        ))
        true
      case GLFW.GLFW_KEY_2 =>
        setInputProcessor(InputMultiplexer(
          shadowController, this
        ))
        true
      case _ => false
  }
}
