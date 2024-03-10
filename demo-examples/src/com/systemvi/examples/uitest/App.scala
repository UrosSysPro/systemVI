package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.animation.AnimationStates.AnimationState
import com.systemvi.engine.ui.utils.animation.{Animatable, AnimationController, AnimationStates}
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.{BoxDecoration, BoxShadow, Colors}
import com.systemvi.engine.ui.widgets.cupertino.Switch
import com.systemvi.engine.ui.widgets.material.{ProgressBar, Range}
import com.systemvi.engine.ui.widgets.{Column, Container, EdgeInsets, Padding, Row, SizedBox, State, StatefulWidget}
import org.joml.{Vector2f, Vector4f}

/**
 *  Row Column Stack padding sizedBox container Switch Range ProgressBar
 *
 *  Scaffold AppBar Drawer Button Text Image
 *  1. dodati animaciju na switch
 *  2. napraviti Text Widget
 *     --prima String kao parametar
 *     --svi karakteri su iste velicine
 *     --karakteri se crtaju kao kvadratici
 *     --velicina Text widgeta je broj redova*visina karaktera,
 *        broj karaktera u redu * sirina karaktera
 *  3. Button klasa, to je container sa GestureDetector unutra,
 *      --Button prima onClick funkciju
 *      --onclick funkcija se poziva samo ako je razlika izmedju
 *        mouse down i mouse up manja od 300ms
 *  4.AppBar Container unutar kojed je Row,
 *    primat tri parametra trailing,title,actions
 *  5. Scaffold
 *  6. Drawer
 */



class App extends StatefulWidget {
  override def createState(): State = new AppState()
}
class AppState extends State with Animatable{
  var value = 0.4f
  var paused=false
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
    controller.value=0f
    controller.setState(AnimationStates.running)
  }
  private def stateChange(state:AnimationState): Unit = {
    println(state.toString)
  }
  override def build(context:BuildContext): Widget = {
    Container(
      color = Colors.blue700,
      child = Padding(
        padding = EdgeInsets.all(100),
        child = SizedBox(
          child = Column(
            children=Array(
              ProgressBar(value),
              Switch(
                paused,
                onChange = value=>setState{
                  ()=>
                    this.paused=value
                    if(value)controller.setState(AnimationStates.running)
                    else controller.setState(AnimationStates.paused)
                }
              ),
              Container(
                child = SizedBox(size=new Vector2f(100,100)),
                boxDecoration = BoxDecoration(
                  color = Colors.green500,
                  borderRadius = 20,
                  boxShadow = Array(
                    BoxShadow(offset = new Vector2f(10,10),blur = 10,color = Colors.slate800)
                  )
                )
              )
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