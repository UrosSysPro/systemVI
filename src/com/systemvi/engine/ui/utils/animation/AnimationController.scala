package com.systemvi.engine.ui.utils.animation

import com.systemvi.engine.ui.utils.animation.AnimationStates.AnimationState

class AnimationController(val duration:Float,val onStateChange:AnimationState=>Unit,val onValueChange:Float=>Unit) {
  //value je u sekundama
  var value:Float=0
  var state=AnimationStates.start
  def update(delta:Float): Unit = {
    state match {
      case AnimationStates.running=>
        value+=delta*duration
//        if(value>duration)
      case AnimationStates.reverse=>
      case _=>
    }
  }
}
object AnimationController{
  def apply(
             minutes:Float=0,
             seconds:Float=0,
             milliseconds:Float=0,
             microseconds:Float=0,
             onStateChange:AnimationState=>Unit=_=>{},
             onValueChange:Float=>Unit=_=>{}
           ):AnimationController=
    new AnimationController(minutes*60+seconds+milliseconds/1000f+microseconds/1000000f,onStateChange, onValueChange)
}

object AnimationStates extends Enumeration {
  type AnimationState=Value
  val running=Value(0,"running")
  val start=Value(1,"start")
  val end=Value(2,"end")
  val paused=Value(3,"paused")
  val reverse=Value(4,"reverse")
}