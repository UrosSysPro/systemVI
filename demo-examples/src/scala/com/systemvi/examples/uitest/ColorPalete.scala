package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.{BoxDecoration, Colors}
import com.systemvi.engine.ui.widgets.{Container, Expanded, Row, SizedBox, StatelessWidget}


object ColorPalete{
  def apply(): ColorPalete = new ColorPalete()
}
class ColorPalete extends StatelessWidget{
  override def build(context: BuildContext): Widget = {
    val colors=Array(
      Colors.pink50,
      Colors.pink100,
      Colors.pink200,
      Colors.pink300,
      Colors.pink400,
      Colors.pink500,
      Colors.pink600,
      Colors.pink700
    )
    SizedBox(
      width = 500,height=70,
      child = Row(
        children = colors.map(color=>
          Expanded(
            child=Container(
              decoration = BoxDecoration(
                borderRadius = 4,
                color=color
              )
            )
          )
        )
      )
    )
  }
}
