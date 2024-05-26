package com.systemvi.examples.compute.raymarching

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.renderers.TextureRenderer
import com.systemvi.engine.shader.Shader
import com.systemvi.engine.texture.{Format, Texture}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.{Barrier, Buffer}
import com.systemvi.engine.window.Window

class App extends Game(4,3,60,800,600,"Ray Marching"){
  var texture:Texture=null
  var controller:CameraController3=null
  var rendererCamera:Camera3=null
  var textureRenderer:TextureRenderer=null
  var shader:Shader=null
  override def setup(window: Window): Unit = {
    texture=new Texture(window.getWidth,window.getHeight,Format.RGBA32F)
    textureRenderer=new TextureRenderer()
    rendererCamera=Camera3.builder2d()
      .size(window.getWidth,window.getHeight)
      .position(window.getWidth/2,window.getHeight/2)
      .scale(1,1)
      .build()
    shader=Shader.builder()
      .compute("assets/examples/compute/raymarching/compute.glsl")
      .build()
    textureRenderer.projection(rendererCamera.projection).view(rendererCamera.view)
    controller=CameraController3.builder()
      .window(window)
      .build()
    setInputProcessor(controller)
  }
  override def loop(delta: Float): Unit = {
    controller.update(delta);
    Utils.clear(0.3f,0.6f,0.9f,1.0f,Buffer.COLOR_BUFFER)
    shader.use()
    shader.setUniform("view",controller.camera.view)
    texture.bindAsImage(0)
    shader.dispatch(800/8,600/8,1)
    Utils.barrier(Barrier.IMAGE_ACCESS)
    textureRenderer.draw(texture,0,0,texture.getWidth,texture.getHeight)
    textureRenderer.flush()
    print(s"\r$getFPS    $getFrameTime")
  }
}
