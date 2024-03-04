package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Padding, Range, Row, SizedBox, State, StatefulWidget}
import org.joml.Vector4f

class App extends StatefulWidget {
  override def createState(): State = new AppState(this)
}
class AppState(app:StatefulWidget)extends State(app){
  var value = 30f
  override def build(): Widget = {
    Container(
      color = new Vector4f(0.3f,0.6f,0.9f,1.0f),
      child = Padding(
        padding = EdgeInsets.all(150),
        child = SizedBox(
          child = Row(
            children=Array(
              new Hover(),
              new Hover(),
              new Hover()
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