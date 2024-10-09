package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.animation.Animatable
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.{ BoxDecoration, Colors, CrossAxisAlignment, MainAxisAlignment}
import com.systemvi.engine.ui.widgets.cupertino.Switch
import com.systemvi.engine.ui.widgets.{Column, Container, EdgeInsets, Expanded, Padding, Row, SizedBox, State, StatefulWidget, Text, TextStyle}

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
object App{
  def apply(): App = new App()
}
class AppState extends State with Animatable{
  var value = true
  override def build(context:BuildContext): Widget = {


    Container(
      color=Colors.white,
      child = SizedBox(
        child=Column(
          crossAxisAlignment=CrossAxisAlignment.center,
          mainAxisAlignment=MainAxisAlignment.start,
          children = Array(
            Switch(value, onChange = value => setState{()=> this.value=value}),
            NavBar(),
            SizedBox(height = 40),
            Text("Tailwind CSS\nColor Generator",font=WidgetTest.font),
            SizedBox(height = 30),
            Text("Press spacebar, enter a hexcode or change the\nHSL values to create a custom color scale",style=TextStyle(color = Colors.gray400),WidgetTest.font),
            SizedBox(height = 30),
            ColorInput(),
            SizedBox(
              width = 500,height=100,
              child=Row(
                mainAxisAlignment=MainAxisAlignment.spaceBetween,
                children=Array(
                  Text("Values",font = WidgetTest.font),
                  Text("Colors",font=WidgetTest.font)
                )
              )
            ),
            ColorPalete(),
            SizedBox(
              width=800,
              height = 300,
              child=Row(
                children=Array(
                  Expanded(
                    child=Padding(
                      padding = EdgeInsets.all(5),
                      child=Container(
                        decoration = BoxDecoration(
                          borderRadius = 15,
                          color=Colors.pink300
                        ),
                        child=Padding(
                          padding=EdgeInsets.only(top = 30,left = 15),
                          child=SizedBox(
                            child=Column(
                              crossAxisAlignment=CrossAxisAlignment.start,
                              children = Array(
                                Text("Header1",style=TextStyle(),WidgetTest.font),
                                SizedBox(height = 20),
                                Text("Header2",style=TextStyle(color=Colors.gray400),WidgetTest.font)
                              )
                            )
                          )
                        )
                      )
                    )
                  ),
                  Expanded(
                    child=Padding(
                      padding = EdgeInsets.all(5),
                      child=Container(
                        decoration = BoxDecoration(
                          borderRadius = 15,
                          color=Colors.pink500
                        )
                      )
                    )
                  ),
                  Expanded(
                    child=Padding(
                      padding = EdgeInsets.all(5),
                      child=Container(
                        decoration = BoxDecoration(
                          borderRadius = 15,
                          color=Colors.pink600
                        )
                      )
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
}