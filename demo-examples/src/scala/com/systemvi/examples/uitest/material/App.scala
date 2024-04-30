package com.systemvi.examples.uitest.material

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.{Scene, Widget}
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.{BoxDecoration, Colors}
import com.systemvi.engine.ui.utils.font.Font
import com.systemvi.engine.ui.widgets.material.{AppBar, Scaffold}
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Padding, SizedBox, State, StatefulWidget, Text, TextStyle}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window

object App{
  val font=Font.load(
    "assets/examples/widgetRenderer2Test/font.PNG",
    "assets/examples/widgetRenderer2Test/font.json"
  )
}
class App extends Game(3,3,60,800,600,"Material App"){
  var scene:Scene=null
  override def setup(window: Window): Unit = {
    scene=Scene(
      initialWidth = window.getWidth,
      initialHeight = window.getHeight,
      font=App.font,
      root = MyApp()
    )
  }
  override def loop(delta: Float): Unit = {
    Utils.clear(0,0,0,0,Buffer.COLOR_BUFFER)
    scene.animate(delta)
    scene.resize(scene.width,scene.height)
    scene.draw()
  }

  override def resize(width: Int, height: Int): Boolean = scene.resize(width,height)
}
object MyApp{
  def apply(): MyApp = new MyApp()
}
class MyApp() extends StatefulWidget{
  override def createState(): State = new MyAppState()
}

class MyAppState extends State{
  override def build(context: BuildContext): Widget = {
    Scaffold(appBar=AppBar(
      leading=Padding(
        padding = EdgeInsets.symetric(horizontal = 20),
        child = SizedBox(
          width = 40,
          height = 40,
          child = Container(
            decoration = BoxDecoration(
              borderRadius = 10,
              color = Colors.green500
            )
          )
        )
      ),
      title = Text("AppBar",style = TextStyle(scale = 0.5f),font = App.font),
      actions = Array(
        Padding(
          padding = EdgeInsets.only(right = 20),
          child = SizedBox(
            width = 40,
            height = 40,
            child = Container(
              decoration = BoxDecoration(
                borderRadius = 10,
                color = Colors.green500
              )
            )
          )
        ),
        Padding(
          padding = EdgeInsets.only(right = 20),
          child = SizedBox(
            width = 40,
            height = 40,
            child = Container(
              decoration = BoxDecoration(
                borderRadius = 10,
                color = Colors.green500
              )
            )
          )
        ),
        Padding(
          padding = EdgeInsets.only(right = 20),
          child = SizedBox(
            width = 40,
            height = 40,
            child = Container(
              decoration = BoxDecoration(
                borderRadius = 10,
                color = Colors.green500
              )
            )
          )
        )
      )
    ))
  }
}
