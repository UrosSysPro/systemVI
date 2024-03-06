package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.Widget

class GestureDetector(
                       child:Widget,
                       val mouseDown:(Int,Int,Double,Double)=>Boolean,
                       val mouseUp:(Int,Int,Double,Double)=>Boolean,
                       val mouseMove:(Double,Double)=>Boolean,
                       val mouseEnter:()=>Unit,
                       val mouseLeave:()=>Unit,
                       val scroll:(Double,Double)=>Boolean,
                       val keyDown:(Int,Int,Int)=> Boolean,
                       val keyUp:(Int,Int,Int)=> Boolean,
                       val focusable:Boolean=true,
                       var mouseOver:Boolean=false
                     ) extends StatelessWidget() {

  override def build(): Widget = child
}

object GestureDetector{
  def apply(
             child: Widget=null,
             mouseDown:(Int,Int,Double,Double)=>Boolean=(_,_,_,_)=>false,
             mouseUp:(Int,Int,Double,Double)=>Boolean=(_,_,_,_)=>false,
             mouseMove:(Double,Double)=>Boolean=(_,_)=>false,
             mouseEnter:()=>Unit=()=>{},
             mouseLeave:()=>Unit=()=>{},
             scroll:(Double,Double)=>Boolean=(_,_)=>false,
             keyDown:(Int,Int,Int)=> Boolean=(_,_,_)=>false,
             keyUp:(Int,Int,Int)=> Boolean=(_,_,_)=>false,
             focusable:Boolean=true
           ): GestureDetector = new GestureDetector(child, mouseDown, mouseUp, mouseMove, mouseEnter, mouseLeave, scroll, keyDown, keyUp, focusable)
}
