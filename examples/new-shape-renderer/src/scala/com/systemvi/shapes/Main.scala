package com.systemvi.shapes

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.texture.Texture.Repeat
import com.systemvi.engine.texture.{Texture, TextureRegion}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.{Matrix4f, Vector2f}

object Main extends Game(3,3,60,800,600,"Polygon Renderer"){

  var shapeRenderer:ShapeRenderer=null
  var texture:Texture=null
  var camera:Camera3=null
  var textureRegion:Array[Array[TextureRegion]]=null
  var emptyRegion:TextureRegion=null

  override def setup(window: Window): Unit = {
    val width=window.getWidth.toFloat
    val height=window.getHeight.toFloat
    camera=Camera3.builder2d()
      .position(width/2,height/2)
      .size(width, height)
      .build()
    camera.orthographic(-width/2,width/2,-height/2,height/2,-1000,1000)
    shapeRenderer=ShapeRenderer()
    texture=Texture.builder()
      .file("assets/tiles.png")
      .borderColor(Colors.white)
      .verticalRepeat(Repeat.CLAMP_BORDER)
      .horizontalRepeat(Repeat.CLAMP_BORDER)
      .build()
    textureRegion=TextureRegion.split(texture,18,18)
    emptyRegion=TextureRegion(texture,-1,-1,0,0)
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.black,Buffer.COLOR_BUFFER)
    shapeRenderer.texture=texture
    shapeRenderer.view(camera.view)
    shapeRenderer.projection(camera.projection)

    val time=Utils.getTime

    for(i<-0 until 5){
      val size=50f
      val x=i*size+50f
      val y=50f
      shapeRenderer.draw(Square(x,y,40,Colors.white,textureRegion(5)(0)))
//      shapeRenderer.draw(Square(x,y,40,Colors.red500,emptyRegion))
    }
    
    shapeRenderer.flush()
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}