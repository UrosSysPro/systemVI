package com.systemvi.engine.ui.utils.animation

import com.systemvi.engine.ui.utils.animation.AnimationStates.{AnimationState, start}

class AnimationController(val duration:Float,val onStateChange:AnimationState=>Unit,val onValueChange:Float=>Unit) {
  //value je u sekundama
  var value:Float=0
  private var state:AnimationStates.Value =AnimationStates.start
  def update(delta:Float): Unit = {
    state match {
      case AnimationStates.running=>
        value+=delta/duration
        if(value>1){
          value=1
          state=AnimationStates.end
          onValueChange(value)
          onStateChange(state)
        }else{
          onValueChange(value)
        }
      case AnimationStates.reverse=>
        value-=delta/duration
        if(value<0){
          value=0
          state=AnimationStates.start
          onValueChange(value)
          onStateChange(state)
        }else{
          onValueChange(value)
        }
      case _=>
    }
  }
  def setState(state:AnimationState): Unit = {
    this.state=state
    state match {
      case AnimationStates.start=>value=0
      case AnimationStates.end=>value=1
      case _=>
    }
    onStateChange(state)
  }
  def getState: AnimationStates.Value =state
}
object AnimationController{
  def apply(
             animatable: Animatable,
             minutes:Float=0,
             seconds:Float=0,
             milliseconds:Float=0,
             microseconds:Float=0,
             onStateChange:AnimationState=>Unit=_=>{},
             onValueChange:Float=>Unit=_=>{}
           ):AnimationController= {
    val controller = new AnimationController(minutes * 60 + seconds + milliseconds / 1000f + microseconds / 1000000f, onStateChange, onValueChange)
    animatable.register(controller)
    controller
  }
}

object AnimationStates extends Enumeration {
  type AnimationState=Value
  val running=Value(0,"running")
  val start=Value(1,"start")
  val end=Value(2,"end")
  val paused=Value(3,"paused")
  val reverse=Value(4,"reverse")
}