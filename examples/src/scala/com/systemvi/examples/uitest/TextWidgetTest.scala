package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.utils.animation.{Animatable, AnimationController, AnimationStates}
import com.systemvi.engine.ui.{Scene, Widget}
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.{BoxDecoration, Colors}
import com.systemvi.engine.ui.utils.font.Font
import com.systemvi.engine.ui.widgets.{Center, Container, EdgeInsets, Expanded, Padding, Row, State, StatefulWidget, Text, TextStyle, Transform}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.Vector2f


object TextWidgetTest{
  val font=Font.load(
    "assets/examples/widgetRenderer2Test/font.PNG",
    "assets/examples/widgetRenderer2Test/font.json"
  )
}

class TextWidgetTest extends Game(3,3,60,800,600,"Text Widget"){
  var scene:Scene=null
  var time:Float=0

  override def setup(window: Window): Unit = {
    scene=Scene(
      initialWidth = window.getWidth,
      initialHeight = window.getHeight,
      font=TextWidgetTest.font,
      root=new TransformsTest()
    )
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(0,0,0,1,Buffer.COLOR_BUFFER)
    scene.animate(delta)
    scene.resize(scene.width,scene.height)
    scene.draw()
  }

  override def resize(width: Int, height: Int): Boolean = {
    scene.resize(width,height)
  }
}

class TransformsTest extends StatefulWidget {
  override def createState(): State = new TransformsTestState()
}
class TransformsTestState extends State with Animatable{
  var controller:AnimationController=null
  override def init(): Unit = {
    controller=AnimationController(
      animatable = this,
      seconds = 5,
      onStateChange = state => if(state==AnimationStates.end) {
        controller.value=0
        controller.setState(AnimationStates.running)
      },
      onValueChange = _ => setState(()=>{})
    )
    controller.setState(AnimationStates.running)
  }
  override def build(context: BuildContext): Widget = Row(
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
                  font=TextWidgetTest.font,
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
                  font=TextWidgetTest.font,
                  style = TextStyle(scale = 0.25f)
                )
              )
            )
          )
        )
      ),
      Expanded(
        Center(
          child=Transform(
            rotate = controller.value,
            scale = new Vector2f(controller.value*2,1),
            translate = new Vector2f(controller.value*100,0),
            child=Container(
              decoration=BoxDecoration(
                color = Colors.green500, borderRadius = 20
              ),
              child = Padding(
                padding=EdgeInsets.symetric(horizontal = 20,vertical = 10),
                child=Text(
                  "Ovo je neki dugacak text\nOvo je neki dugacak text\nOvo je neki dugacak text" +
                    "\nOvo je neki dugacak text\n",
                  font=TextWidgetTest.font,
                  style = TextStyle(scale = 0.25f)
                )
              )
            )
          )
        )
      )
    )
  )
}