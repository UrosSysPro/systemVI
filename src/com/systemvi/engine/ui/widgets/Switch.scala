package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

class Switch(val value:Boolean) extends StatelessWidget {

  override def build(): Widget =
    SizedBox(
      size=new Vector2f(55,30)
    )

  override def draw(renderer: WidgetRenderer): Unit = {
    if(value){
      //nacrtaj selektovan switch
    }else{
      //nacrtaj neselektovan switch
    }
  }
}
