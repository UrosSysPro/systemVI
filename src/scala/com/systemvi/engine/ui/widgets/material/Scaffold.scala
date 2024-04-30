package com.systemvi.engine.ui.widgets.material

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.ui.widgets.{Column, Container, Expanded, Positioned, Stack, State, StatefulWidget}
import org.joml.Vector4f

object Scaffold{
  def apply(appBar: AppBar=null, body: Widget=null, drawer: Widget=null, floatingActionButton: Widget=null,backgroundColor:Vector4f=Colors.white): Scaffold = new Scaffold(appBar, body, drawer, floatingActionButton,backgroundColor)
}
class Scaffold(
              val appBar: AppBar,
              val body: Widget,
              val drawer:Widget,
              val floatingActionButton:Widget,
              val backgroundColor:Vector4f
              ) extends StatefulWidget{
  override def createState(): State = new ScaffoldState()
}

class ScaffoldState extends State{
  override def build(context: BuildContext): Widget = {
    val scaffold=widget match {
      case scaffold: Scaffold=>scaffold
    }
    Container(
      color=scaffold.backgroundColor,
      child=Stack(
      children = Array(
        Column(
          children = Array(
            scaffold.appBar,
            Expanded(child=scaffold.body)
          )
        ),
        if(scaffold.floatingActionButton!=null)Positioned(
          right = 10,
          bottom = 10,
          child = scaffold.floatingActionButton
        )else null,
        if(scaffold.drawer!=null)Positioned(
          top = 0,
          bottom = 0,
          left = 0,
          child = scaffold.drawer
        )else null
      )
    ))
  }
}