package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.{BoxDecoration, Colors}
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Padding, SizedBox, StatelessWidget}

class ColorInput extends StatelessWidget{
  override def build(context: BuildContext): Widget = {
    SizedBox(
      width=300,height=50,
      child=Container(
      decoration = BoxDecoration(
        color = Colors.gray400,
        borderRadius = 25
      ),
      child=Padding(
        padding = EdgeInsets.all(2),
        child=Container(
          decoration = BoxDecoration(
            color = Colors.white,
            borderRadius = 23
          ),
          child=Padding(

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
