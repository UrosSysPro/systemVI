package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import org.joml.{Vector2f, Vector4f}

case class TextStyle(
                      fontSize:Float=16,
                      fontWeight:Float=1,
                      color:Vector4f=new Vector4f(0,0,0,1)
                    )
class Text(val text:String="",val style:TextStyle=TextStyle()) extends StatelessWidget {
  override def build(context: BuildContext): Widget = null
  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    val glyphHeight=style.fontSize
    val glyphWidth=glyphHeight*0.6f
    val charSpacing=3f
    var width=0f
    var height=0f
    var lines=0
    for(char<-text){

    }
    size.set(width,height)
  }
  override def draw(context: DrawContext): Unit = {

  }
}

object Text{
  def apply(text: String, style: TextStyle): Text = new Text(text, style)
}
