package com.systemvi.engine.ui.widgets.material

import com.systemvi.engine.ui.{Border, Widget}
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data
import com.systemvi.engine.ui.utils.data.{BoxDecoration, Colors}
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, GestureDetector, Padding, State, StatefulWidget, StatelessWidget}
import org.joml.Vector4f

class Button(val childWidget:Widget,val decoration: BoxDecoration,val onTap:()=>Unit,var padding: EdgeInsets) extends StatefulWidget{
  override def createState(): State = new ButtonState()
}

class ButtonState extends State{
  var mouseDownStart:Long=0
  override def build(context: BuildContext): Widget = widget match {
      case button: Button => GestureDetector(
        child=Container(
        child = Padding(
          padding=button.padding,
          child=button.childWidget
        ),
        decoration = button.decoration
      ),
        mouseDown = (_,_,_,_)=>{
          mouseDownStart=System.currentTimeMillis()
          true
        },
        mouseUp = (_,_,_,_)=>{
          if(System.currentTimeMillis()-mouseDownStart<300){
            button.onTap()
          }
          true
        }
      )
    }
}
object Button{
  def filled(
              child:Widget,
              decoration: BoxDecoration,
              onTap:()=>Unit,
              padding:EdgeInsets=EdgeInsets.symetric(horizontal = 20,vertical = 10)
            ):Button=new Button(childWidget = child, decoration = decoration, onTap = onTap,padding)
  def outlined(
                child:Widget,
                color:Vector4f=Colors.black,
                borderRadius:Float=10,
                borderWidth:Float=2,
                onTap:()=>Unit,
                padding: EdgeInsets=EdgeInsets.symetric(horizontal = 20,vertical = 10)
              ):Button=
    new Button(
      childWidget=child,
      decoration = BoxDecoration(
        color=new Vector4f(color.x,color.y,color.z,0),
        border = data.Border(color,borderWidth),
        borderRadius=borderRadius
      ), onTap = onTap,padding
    )
}