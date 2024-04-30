package com.systemvi.examples.uitest.material

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.{Scene, UIApplication, Widget, runApp}
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.{BoxDecoration, Colors}
import com.systemvi.engine.ui.utils.font.Font
import com.systemvi.engine.ui.widgets.material.{AppBar, Button, FloatingActionButton, Scaffold}
import com.systemvi.engine.ui.widgets.{Center, Container, EdgeInsets, Padding, SizedBox, State, StatefulWidget, Text, TextStyle}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window

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
      appBar=AppBar(
        leading = Padding(
          padding=EdgeInsets.symetric(horizontal = 20),
          child=Text("<",font=UIApplication.font,style = TextStyle(scale = 0.5f))
        ),
        title = Text("AppBar",style = TextStyle(scale = 0.5f),font = UIApplication.font)
      ),
      body=Center(child=Text(s"Clicks: $clicks",font=UIApplication.font)),
      floatingActionButton = FloatingActionButton(
        onTap = ()=>setState(()=>clicks+=1),
        child = Text("+",font = UIApplication.font)
      )
    )
  }
}