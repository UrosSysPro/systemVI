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
                       val focusable:Boolean=true
                     ) extends StatelessWidget() {

  override def build(): Widget = child
}
