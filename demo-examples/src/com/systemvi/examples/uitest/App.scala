package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Padding, Range, Row, SizedBox, State, StatefulWidget, Switch}
import org.joml.Vector4f

class App extends StatefulWidget {
  override def createState(): State = new AppState()
}
class AppState extends State{
  var value = 30f
  override def build(): Widget = {
    Container(
      color = new Vector4f(0.3f,0.6f,0.9f,1.0f),
      child = Padding(
        padding = EdgeInsets.all(0),
        child = SizedBox(
          child = Row(
            children=Array(
              new Hover(),
              new Hover(),
              new Hover(),
              Switch(true),
              Switch(false),
              Switch(true),
              Range(value,onChange = value=> setState(()=>this.value=value))
            )
          )
        )
      )
    )
  }
}
object App{
  def apply(): App = new App()
}