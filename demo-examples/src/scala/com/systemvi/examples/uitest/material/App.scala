package com.systemvi.examples.uitest.material

import com.systemvi.engine.ui.{UIApplication, Widget, runApp}
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.ui.widgets.material.{AppBar, FloatingActionButton, Scaffold}
import com.systemvi.engine.ui.widgets.{Center, EdgeInsets, Padding, State, StatefulWidget, Text, TextStyle}

object App{
  def main(): Unit = {
    runApp("MyApp",MyApp())
  }
}
object MyApp{
  def apply(): MyApp = new MyApp()
}
class MyApp() extends StatefulWidget{
  override def createState(): State = new MyAppState()
}
class MyAppState extends State{
  var clicks:Int=0
  override def build(context: BuildContext): Widget = {
    Scaffold(
      appBar = AppBar(
        leading = Padding(
          padding = EdgeInsets.symetric(horizontal = 20),
          child = Text("<",
            font = UIApplication.font,
            style = TextStyle(scale = 0.5f, color = Colors.white)
          )
        ),
        title = Text("AppBar",
          style = TextStyle(scale = 0.5f, color = Colors.white),
          font = UIApplication.font
        ),
        actions = Array(

        )
      ),
      body = Center(
        child = Text(s"$clicks",
          font = UIApplication.font,
          style = TextStyle()
        )
      ),
      floatingActionButton = FloatingActionButton(
        onTap = () => setState(() => clicks += 1),
        child = Text("+", font = UIApplication.font,style = TextStyle(color = Colors.white))
      )
    )
  }
}