package com.systemvi.uitests

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.*
import com.systemvi.engine.ui.widgets.*
import org.joml.Vector2f

class ColorInput extends StatelessWidget{
  override def build(context: BuildContext): Widget = {
    SizedBox(
      width=300,height=50,
      child=Container(
      decoration = BoxDecoration(
        color = Colors.gray400,
        borderRadius = 25,
        boxShadow = Array(
          BoxShadow(
            offset = new Vector2f(0,5),
            blur = 20,
            size = 5,
            color = Colors.slate300
          )
        )
      ),
      child=Padding(
        padding = EdgeInsets.all(1),
        child=Container(
          decoration = BoxDecoration(
            color = Colors.white,
            borderRadius = 24
          ),
          child=Padding(
            padding = EdgeInsets.all(10),
            child = Row(
              crossAxisAlignment = CrossAxisAlignment.center,
              mainAxisAlignment = MainAxisAlignment.spaceBetween,
              children = Array(
                SizedBox(
                  width = 30,height = 30,
                  child=Container(decoration=BoxDecoration(color = Colors.pink500,borderRadius = 15))
                ),
                Text("A",font = WidgetTest.font)
              )
            )
          )
        )
      )
    )
    )
  }
}
object ColorInput{
  def apply(): ColorInput = new ColorInput()
}
