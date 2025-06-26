package com.systemvi.uitests

import com.systemvi.engine.ui.runApp
import com.systemvi.engine.ui.utils.data.{BoxDecoration, Colors}
import com.systemvi.engine.ui.widgets.{Center, Container, SizedBox}
import com.systemvi.engine.ui.widgets.material.{AppBar, Button, FloatingActionButton, Scaffold}

object Main {
  def main(args: Array[String]): Unit = {
    runApp("window title",
      Scaffold(
        appBar = AppBar(
          title = Container(

          )
        ),
        body = Center(
          Button.filled(
            onTap = ()=>{println("hello")},
            child = SizedBox(width = 50,height = 30),
            decoration = BoxDecoration(color = Colors.blue500,borderRadius = 10)
          )
        ),
        floatingActionButton = FloatingActionButton(
          onTap = ()=>{println("hello")},
          child = Container()
        )
      )
    )
  }
}