package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.animation.AnimationStates.AnimationState
import com.systemvi.engine.ui.utils.animation.{Animatable, AnimationController, AnimationStates}
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Padding, ProgressBar, Range, Row, SizedBox, State, StatefulWidget, Switch}
import org.joml.Vector4f

class App extends StatefulWidget {
  override def createState(): State = new AppState()
}
class AppState extends State with Animatable{
  var value = 0.4f
  var controller:AnimationController=null
  override def init(): Unit = {
    controller=AnimationController(
      animatable=this,
      seconds = 10,
      onStateChange=stateChange,
      onValueChange = value=> {
        setState {
          () => {
            this.value = value
          }
        }
      }
    )
    controller.value=0.5f
    controller.setState(AnimationStates.running)
  }
  private def stateChange(state:AnimationState): Unit = {
    println(state.toString)
  }
  override def build(context:BuildContext): Widget = {
    Container(
      color = new Vector4f(0.3f,0.6f,0.9f,1.0f),
      child = Padding(
        padding = EdgeInsets.all(100),
        child = SizedBox(
          child =ProgressBar(value)
        )
      )
    )
  }
}
object App{
  def apply(): App = new App()
}