package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.utils.font.Font
import org.joml.{Vector2f, Vector4f}
case class TextStyle(
//                      fontSize:Float=16,
//                      fontWeight:Float=1,
                      color:Vector4f=new Vector4f(0,0,0,1)
//                      charSpacing:Float=1,
//                      lineSpacing:Float=2
                    )
class Text(val text:String="",val style:TextStyle=TextStyle(),val font: Font) extends StatelessWidget {
  override def build(context: BuildContext): Widget = null
  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    val glyphHeight=font.config.charHeight
    val charSpacing=font.config.charSpacing
    var x=0f
    var y=0f
    var width=0f
    var height=glyphHeight
    var newLine=false
    var lines=1
    for(char<-text){
      if(newLine){
        x=0
        lines+=1
        y+=glyphHeight+font.config.lineSpacing
        height+=glyphHeight+font.config.lineSpacing
        newLine=false
      }
      char match {
        case '\n'=>newLine=true
        case _=>
          val s=font.symbols.find(s=>s.id.toChar==char).get
          x+=s.xadvance+charSpacing
      }
      width=Math.min(Math.max(x,width),maxParentSize.x)
      if(x>maxParentSize.x)newLine=true
    }
    size.set(width,height)
  }
  override def draw(context: DrawContext): Unit = {
    val glyphHeight=font.config.charHeight
    val charSpacing=font.config.charSpacing
    val lineSpacing=font.config.lineSpacing
    var x=0f
    var y=0f
    var newLine=false
    var lines=1
    for(char<-text){
      if(newLine){
        x=0
        lines+=1
        y+=glyphHeight+lineSpacing
        newLine=false
      }
      char match {
        case '\n'=>newLine=true
        case ' '=>
          val s=font.symbols.find(s=>s.id.toChar==char).get
          x+=s.xadvance
        case char:Char=>
          val s=font.symbols.find(s=>s.id.toChar==char).get
          context.renderer.rect(
            x+position.x+s.width.toFloat/2+s.xoffset,
            y+position.y+s.height.toFloat/2+s.yoffset,
            s.width,
            s.height,
            style.color
          )
          x+=s.xadvance+charSpacing
      }
      if(x>size.x)newLine=true
    }
  }
}

object Text {
  def apply(text: String = "", style: TextStyle = TextStyle(),font: Font): Text = new Text(text, style,font)
}

