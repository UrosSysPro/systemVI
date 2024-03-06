package com.systemvi.engine.ui.utils.animation

class AnimationController(val duration:Float,val onStateChange:()=>Unit,val onValueChange:()=>Unit) {
  //value je u sekundama
  var value:Float=0

}
object AnimationController{
  def apply(
             minutes:Float=0,
             seconds:Float=0,
             milliseconds:Float=0,
             microseconds:Float=0,
             onStateChange:()=>Unit=()=>{},
             onValueChange:()=>Unit=()=>{}
           ):AnimationController=
    new AnimationController(minutes*60+seconds+milliseconds/1000f+microseconds/1000000f,onStateChange, onValueChange)
}
