package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.Widget

class GestureDetector(
                       child:Widget,
                       val mouseDown:(Int,Int,Double,Double)=>Boolean=(_,_,_,_)=>false,
                       val mouseUp:(Int,Int,Double,Double)=>Boolean=(_,_,_,_)=>false,
                       val mouseMove:(Double,Double)=>Boolean=(_,_)=>false,
                       val mouseEnter:()=>Boolean=()=>false,
                       val mouseLeave:()=>Boolean=()=>false,
                       val scroll:(Double,Double)=>Boolean=(_,_)=>false,
                       val keyDown:(Int,Int,Int)=> Boolean=(_,_,_)=>false,
                       val keyUp:(Int,Int,Int)=> Boolean=(_,_,_)=>false,
                       val focusable:Boolean=true,
                       var mouseOver:Boolean=false
                     ) extends StatelessWidget() {

  override def build(): Widget = child
}

object GestureDetector{
  def apply(
             child: Widget,
             mouseDown:(Int,Int,Double,Double)=>Boolean=(_,_,_,_)=>false,
             mouseUp:(Int,Int,Double,Double)=>Boolean=(_,_,_,_)=>false,
             mouseMove:(Double,Double)=>Boolean=(_,_)=>false,
             mouseEnter:()=>Boolean=()=>false,
             mouseLeave:()=>Boolean=()=>false,
             scroll:(Double,Double)=>Boolean=(_,_)=>false,
             keyDown:(Int,Int,Int)=> Boolean=(_,_,_)=>false,
             keyUp:(Int,Int,Int)=> Boolean=(_,_,_)=>false,
             focusable:Boolean=true
           ): GestureDetector = new GestureDetector(child, mouseDown, mouseUp, mouseMove, mouseEnter, mouseLeave, scroll, keyDown, keyUp, focusable)
}
