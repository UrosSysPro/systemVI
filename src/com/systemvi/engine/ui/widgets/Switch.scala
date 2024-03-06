package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.widgets.Switch.{padding, selectedColor, unselectedColor}
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.{Vector2f, Vector4f}

class Switch(val value:Boolean,val onChange:Boolean=>Unit) extends StatefulWidget {
  override def createState(): State = new SwitchState()
}

class SwitchState extends State{

  override def init(): Unit = {
    println(s"init switch")
  }

  override def dispose(): Unit = {
    println(s"dispose switch")
  }
  override def build(): Widget =
    SizedBox(
      size=new Vector2f(55,30),
      child = GestureDetector(
        mouseDown = (_,_,_,_)=>{
          val switch=widget match {
            case switch: Switch=>switch
          }
          switch.onChange(!switch.value)
          true
        }
      )
    )
  override def draw(renderer: WidgetRenderer): Unit = {
    val value=widget match {
      case switch: Switch=>switch.value
    }
    val size=widget.size
    val position=widget.position
    val circleSize:Float = size.y
    val x:Float = if (value) position.x + size.x - circleSize else position.x
    val y:Float = position.y
    //background
    renderer.rect(
      position.x,
      position.y,
      size.x,
      size.y,
      if(value)selectedColor else unselectedColor,
      size.y/2
    )
    val shadowBlur:Float=4
    val shadowSize:Float=4
    //shadow
    renderer.rect(
      x+padding-shadowSize,
      y+padding-shadowSize,
      circleSize-2*padding+shadowSize*2,
      circleSize-2*padding+shadowSize*2,
      new Vector4f(0.5f),
      circleSize/2-padding+shadowSize,
      shadowBlur
    )
    //circle
    renderer.rect(
      x+padding,
      y+padding,
      circleSize-2*padding,
      circleSize-2*padding,
      new Vector4f(1.0f),
      (circleSize-2*padding)/2
    )
  }
}

object Switch{
  val selectedColor=new Vector4f(0.2f,0.8f,0.5f,1.0f)
  val unselectedColor=new Vector4f(0.8f,0.8f,0.8f,1.0f)
  val padding=2
  def apply(value: Boolean,onChange:Boolean=>Unit=_=>{}): Switch = new Switch(value,onChange)
}