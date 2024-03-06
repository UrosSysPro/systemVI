package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.{Vector2f, Vector4f}

class Range(
             val value:Float,
             val max:Float,
             val min:Float,
             val step:Float,
             val onChange:Float=>Unit
           ) extends StatefulWidget {
  override def createState(): State = new RangeState()
}
class RangeState extends State {
  var mouseDown=false
  override def build(): Widget = {
    GestureDetector(
      mouseDown=(button,mods,x,y)=>{
        mouseDown=true
        true
      },
      mouseUp=(button,mods,x,y)=>{
        mouseDown=false
        true
      },
      mouseMove=(x,y)=>{
        if(mouseDown){
          val width=widget.size.x
          val heigth=widget.size.y
          val r=heigth/2
          val lineWidth=width-2*r
          val p=(x-r)/lineWidth
          val range=widget match {
            case range: Range=>range
          }
          val value=p*(range.max-range.min)+range.min
          range.onChange(value.toFloat)
        }
        true
      },
      child = SizedBox(
        size = new Vector2f(200,40)
      )
    )
  }
  override def draw(renderer: WidgetRenderer): Unit = {
    val width=widget.size.x
    val heigth=widget.size.y
    val r=heigth/2
    val lineWidth=width-2*r
    val lineHeight=10
    //line
    renderer.rect(
      widget.position.x+r,
      widget.position.y+r-lineHeight/2,
      lineWidth,
      lineHeight,
      new Vector4f(0,0,0,1),
      lineHeight/2
    )
    val range=widget match {
      case range: Range=>range
    }
    val p=(range.value-range.min)/(range.max-range.min)
    //circle
    renderer.rect(
      widget.position.x+p*lineWidth,
      widget.position.y,
      r*2,r*2,new Vector4f(1),
      r
    )
  }
}
object Range{
  def apply(value: Float=50, max: Float=100, min: Float=0, step: Float=1, onChange: Float => Unit=_=>{}): Range = new Range(value, max, min, step, onChange)
}