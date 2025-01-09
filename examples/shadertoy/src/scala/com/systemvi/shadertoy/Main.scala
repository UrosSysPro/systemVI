package com.systemvi.shadertoy

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.Vector2f

object Main extends Game(3, 3, 60, 800, 600, "Shader Toy") {
  var shader: Shader = null
  var vertexArray: VertexArray = null
  var time:Double=3
  override def setup(window: Window): Unit = {
    vertexArray=VertexArray()
    shader = Shader.builder()
      .vertexSource(
        """
        #version 330 core
        void main(){
          int index=gl_VertexID;
          float x,y;
          if(index==0){x = -1.0; y = 1.0;}
          if(index==1){x =  1.0; y = 1.0;}
          if(index==2){x = -1.0; y =-1.0;}
          if(index==3){x =  1.0; y =-1.0;}
          gl_Position=vec4(x,y,0.0,1.0);
        }
        """)
      .fragmentSource(
        s"""
          #version 330 core
          uniform float iTime;
          uniform vec2 iResolution;

          ${Utils.readInternal("rainbow/main.glsl")}

          out vec4 fragColor;
          void main(){
            vec4 fragCoord=gl_FragCoord;
            vec4 color;
            mainImage(color,fragCoord.xy);
            fragColor=color;
          }
        """)
      .build()
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.black, Buffer.COLOR_BUFFER)
    time=time+delta.toDouble
    val window=getWindow
    vertexArray.bind()
    shader.use()
    shader.setUniform("iTime",time)
    shader.setUniform("iResolution",Vector2f(window.getWidth.toFloat,window.getHeight.toFloat))
    shader.drawArrays(Primitive.TRIANGLE_STRIP,4)
  }

  def main(args: Array[String]): Unit = run()
}
