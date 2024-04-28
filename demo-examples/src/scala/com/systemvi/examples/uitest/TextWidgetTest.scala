package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.Scene
import com.systemvi.engine.ui.utils.data.{BoxDecoration, Colors}
import com.systemvi.engine.ui.utils.font.Font
import com.systemvi.engine.ui.widgets.{Center, Container, EdgeInsets, Expanded, Padding, Row, Text, TextStyle, Transform}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window

class TextWidgetTest extends Game(3,3,60,800,600,"Text Widget"){
  var scene:Scene=null

  override def setup(window: Window): Unit = {
    val font=Font.load(
      "assets/examples/widgetRenderer2Test/font.PNG",
      "assets/examples/widgetRenderer2Test/font.json"
    )
    scene=Scene(
      initialWidth = window.getWidth,
      initialHeight = window.getHeight,
      font=font,
      root=Row(
        children=Array(
          Expanded(
            Center(
              child=Transform.rotate(
                rotate=0.1f,
                child=Container(
                  decoration=BoxDecoration(
                    color = Colors.green500, borderRadius = 20
                  ),
                  child = Padding(
                    padding=EdgeInsets.symetric(horizontal = 20,vertical = 10),
                    child=Text(
                      "Ovo je neki dugacak text\nOvo je neki dugacak text\nOvo je neki dugacak text" +
                        "\nOvo je neki dugacak text\n",
                      font=font,
                      style = TextStyle(scale = 0.25f)
                    )
                  )
                )
              )
            )
          ),
          Expanded(
            Center(
              child=Transform.rotate(
                rotate=0.1f,
                child=Container(
                  decoration=BoxDecoration(
                    color = Colors.green500, borderRadius = 20
                  ),
                  child = Padding(
                    padding=EdgeInsets.symetric(horizontal = 20,vertical = 10),
                    child=Text(
                      "Ovo je neki dugacak text\nOvo je neki dugacak text\nOvo je neki dugacak text" +
                        "\nOvo je neki dugacak text\n",
                      font=font,
                      style = TextStyle(scale = 0.25f)
                    )
                  )
                )
              )
            )
          ),
          Expanded(
            Center(
              child=Transform.rotate(
                rotate=0.1f,
                child=Container(
                  decoration=BoxDecoration(
                    color = Colors.green500, borderRadius = 20
                  ),
                  child = Padding(
                    padding=EdgeInsets.symetric(horizontal = 20,vertical = 10),
                    child=Text(
                      "Ovo je neki dugacak text\nOvo je neki dugacak text\nOvo je neki dugacak text" +
                        "\nOvo je neki dugacak text\n",
                      font=font,
                      style = TextStyle(scale = 0.25f)
                    )
                  )
                )
              )
            )
          )
        )
      )
    )
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(0,0,0,1,Buffer.COLOR_BUFFER)
    scene.animate(delta)
    scene.draw()
  }

  override def resize(width: Int, height: Int): Boolean = {
    scene.resize(width,height)
  }
}
