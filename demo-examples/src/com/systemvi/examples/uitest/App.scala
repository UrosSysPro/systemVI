package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Padding, Range, Row, SizedBox, State, StatefulWidget, Switch}
import org.joml.Vector4f

class App extends StatefulWidget {
  override def createState(): State = new AppState()
}
class AppState extends State{
  var value = 30f
  var switch1=true
  var switch2=true
  var switch3=true
  var switch4=true

  override def build(context:BuildContext): Widget = {
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
              Switch(switch1,value=>setState(()=>switch1=value)),
              Switch(switch2,value=>setState(()=>switch2=value)),
              Switch(switch3,value=>setState(()=>switch3=value)),
              Range(value,onChange = value=> setState(()=>this.value=value)),
              if(switch3)Switch(switch4,value=>setState(()=>switch4=value))else null
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