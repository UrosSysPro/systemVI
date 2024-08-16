package com.systemvi.engine.ui.widgets.material

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.data.{Border, BoxDecoration, BoxShadow, Colors}
import com.systemvi.engine.ui.widgets.{Center, EdgeInsets, SizedBox}
import org.joml.{Vector2f, Vector4f}

object FloatingActionButton {
  def apply(
             child:Widget,
             color:Vector4f=Colors.blue500,
             onTap:()=>Unit
           ): Button=Button.filled(
    child= SizedBox(
      width = 30,height = 30,child=Center(child=child)
    ),
    onTap=onTap,
    decoration = BoxDecoration(
      color=color,
      borderRadius = 15,
      border = Border(
        width = 0,
        color = color
      ),
      boxShadow = Array(
        BoxShadow(
          offset = new Vector2f(0,5),
          size = 5,
          blur = 10,
          color=new Vector4f(0,0,0,0.3f)
        )
      )
    ),
    padding = EdgeInsets.all(20)
  )
}
