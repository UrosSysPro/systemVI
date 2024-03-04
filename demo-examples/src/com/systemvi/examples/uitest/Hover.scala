package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.widgets.{Container, GestureDetector, SizedBox, State, StatefulWidget}
import org.joml.{Vector2f, Vector4f}

class Hover extends StatefulWidget{
  override def createState(): State = new HoverState(this)
}

class HoverState(w:StatefulWidget) extends State(w){
  val color = new Vector4f(0.2f, 0.7f, 0.3f, 1.0f)
  override def build(): Widget = GestureDetector(
    mouseEnter=()=>{
//      setState(()=>{
        println("enter")
        color.set(0.6f, 0.7f, 0.2f, 1.0f)
//      })
      true
    },
    mouseLeave=()=>{
//      setState(()=>{
        println("leave")
        color.set(0.2f, 0.7f, 0.3f, 1.0f)
//      })
      true
    },
    child = SizedBox(
      size = new Vector2f(100,100),
      child = Container(
        color = color
      )
    )
  )
}
