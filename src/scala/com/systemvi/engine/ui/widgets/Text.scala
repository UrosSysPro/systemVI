package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import org.joml.{Vector2f, Vector4f}
case class TextStyle(
                      fontSize:Float=16,
                      fontWeight:Float=1,
                      color:Vector4f=new Vector4f(0,0,0,1),
                      charSpacing:Float=1,
                      lineSpacing:Float=2
                    )
class Text(val text:String="",val style:TextStyle=TextStyle()) extends StatelessWidget {
  override def build(context: BuildContext): Widget = null
  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    val glyphHeight=style.fontSize
    val glyphWidth=glyphHeight*0.6f
    val charSpacing=style.charSpacing
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
        y+=glyphHeight+style.lineSpacing
        height+=glyphHeight+style.lineSpacing
        newLine=false
      }
      char match {
        case '\n'=>newLine=true
        case _=> x+=glyphWidth+charSpacing
      }
      width=Math.min(Math.max(x,width),maxParentSize.x)
      if(x>maxParentSize.x)newLine=true
    }
    size.set(width,height)
  }
  override def draw(context: DrawContext): Unit = {
    val glyphHeight=style.fontSize
    val glyphWidth=glyphHeight*0.6f
    val charSpacing=style.charSpacing
    var x=0f
    var y=0f
    var newLine=false
    var lines=1
    for(char<-text){
      if(newLine){
        x=0
        lines+=1
        y+=glyphHeight+style.lineSpacing
        newLine=false
      }
      char match {
        case '\n'=>newLine=true
        case ' '=>
          x+=glyphWidth
        case char:Char=>
          val height=if (char.isUpper)glyphHeight else glyphHeight-4
          context.renderer.rect(x+position.x,y+position.y+glyphHeight-height,glyphWidth,height,style.color,3,1)
          x+=glyphWidth+charSpacing
      }
      if(x>size.x)newLine=true
    }
  }
}

object Text {
  def apply(text: String = "", style: TextStyle = TextStyle()): Text = new Text(text, style)
}

