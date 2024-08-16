package com.systemvi.engine.ui.widgets.cupertino

import com.systemvi.engine.math.Bezier2f
import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.animation.{Animatable, AnimationController, AnimationStates}
import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.widgets.cupertino.Switch.{padding, selectedColor, unselectedColor}
import com.systemvi.engine.ui.widgets.{GestureDetector, SizedBox, State, StatefulWidget}
import org.joml.{Vector2f, Vector4f}

class Switch(val value:Boolean,val onChange:Boolean=>Unit) extends StatefulWidget {
  override def createState(): State = new SwitchState()
}

class SwitchState extends State with Animatable{

  var controller:AnimationController=null

  var timing = new Bezier2f(Array(
    new Vector2f(0f,0f),
    new Vector2f(0.35f,.86f),
    new Vector2f(0f,1.01f),
    new Vector2f(1.0f)
  ))

  override def init(): Unit = {
    controller= AnimationController(
      animatable = this,
      milliseconds=200
    )
  }
  override def build(context:BuildContext): Widget = {
    widget match {
      case switch: Switch=>
        if(!switch.value && controller.getState == AnimationStates.end)controller.setState(AnimationStates.reverse)
        if( switch.value && controller.getState == AnimationStates.start)controller.setState(AnimationStates.running)
    }
    SizedBox(
      width=55,height=30,
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
  }

  override def draw(context:DrawContext): Unit = {
    val value=widget match {
      case switch: Switch=>switch.value
    }
    val d = timing.get(controller.value).y
    val size=widget.size
    val position=widget.position
    val circleSize:Float = size.y
    val x:Float =  d*(position.x + size.x - circleSize)+ (1-d)*position.x
    val y:Float = position.y
    //background
    context.renderer.rect(
      position.x,
      position.y,
      size.x,
      size.y,
      new Vector4f()
        .add(new Vector4f(selectedColor).mul(d))
        .add(new Vector4f(unselectedColor).mul(1-d)),
      size.y/2,blur = 1,
      context
    )
    val shadowBlur:Float=4
    val shadowSize:Float=4
    //shadow
    context.renderer.rect(
      x+padding-shadowSize,
      y+padding-shadowSize,
      circleSize-2*padding+shadowSize*2,
      circleSize-2*padding+shadowSize*2,
      new Vector4f(0.5f),
      circleSize/2-padding+shadowSize,
      shadowBlur,
      context
    )
    //circle
    context.renderer.rect(
      x+padding,
      y+padding,
      circleSize-2*padding,
      circleSize-2*padding,
      new Vector4f(1.0f),
      (circleSize-2*padding)/2,
      blur = 1,
      context
    )
  }
}

object Switch{
  val selectedColor=new Vector4f(0.2f,0.8f,0.5f,1.0f)
  val unselectedColor=new Vector4f(0.8f,0.8f,0.8f,1.0f)
  val padding=2
  def apply(value: Boolean,onChange:Boolean=>Unit=_=>{}): Switch = new Switch(value,onChange)
}