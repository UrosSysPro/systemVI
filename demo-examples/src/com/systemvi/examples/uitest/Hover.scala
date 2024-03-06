package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.widgets.{Container, GestureDetector, SizedBox, State, StatefulWidget}
import org.joml.{Vector2f, Vector4f}

class Hover extends StatefulWidget{
  override def createState(): State = new HoverState()
}

class HoverState extends State{
  val color = new Vector4f(0.2f, 0.7f, 0.3f, 1.0f)
  override def build(): Widget = GestureDetector(
    mouseEnter=()=>{
        println("enter")
        color.set(0.6f, 0.7f, 0.2f, 1.0f)
    },
    mouseLeave=()=>{
        println("leave")
        color.set(0.2f, 0.7f, 0.3f, 1.0f)
    },
    mouseDown=(button,mods,x,y)=>{
      println("mouse down")
      true
    },
    mouseMove=(x,y)=>{
      println(s"mouse move ${x.toInt} ${y.toInt}")
      true
    },
    mouseUp=(button,mods,x,y)=>{
      println("mouse up")
      true
    },
    keyDown=(key,scancode,mods)=>{
      println(key)
      true
    },
    keyUp=(key,scancode,mods)=>{
      println(key)
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
