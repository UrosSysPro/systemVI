package com.systemvi.examples.compute.reactiondiffusion

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.renderers.TextureRenderer
import com.systemvi.engine.shader.Shader
import com.systemvi.engine.texture.{Format, Texture}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.{Barrier, Buffer}
import com.systemvi.engine.window.Window

class App extends Game(4,3,60,800,600,"Reaction diffusion"){
  var current:Texture=null
  var next:Texture=null
  var shader:Shader=null
  var add:Shader=null
  var renderer:TextureRenderer=null
  override def setup(window: Window): Unit = {
    current=new Texture(window.getWidth,window.getHeight,Format.RG16F)
    next=new Texture(window.getWidth,window.getHeight,Format.RG16F)
    shader=Shader.builder()
      .compute("assets/examples/compute/reactiondiffusion/compute.glsl")
      .build()
    add=Shader.builder()
      .compute("assets/examples/compute/reactiondiffusion/add.glsl")
      .build()
    renderer=new TextureRenderer()
    val camera=Camera3.builder2d()
      .size(window.getWidth,window.getHeight)
      .position(window.getWidth/2,window.getHeight/2)
      .build()
    renderer.view(camera.view)
    renderer.projection(camera.projection)
  }
  override def loop(delta: Float): Unit = {
    Utils.clear(1,0,0,0,Buffer.COLOR_BUFFER)

    shader.use()
    next.bindAsImage(0)
    current.bindAsImage(1)
    val p=current
    current=next
    next=p
    shader.dispatch(next.getWidth/8,next.getHeight/8,1)
    Utils.barrier(Barrier.IMAGE_ACCESS)
    renderer.draw(current,0f,0f,800f,600f)
    renderer.flush()
  }
}
