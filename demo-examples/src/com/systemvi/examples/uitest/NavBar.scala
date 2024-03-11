package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Expanded, Padding, Row, SizedBox, StatelessWidget}
import org.joml.Vector2f

class NavBar extends StatelessWidget{
  override def build(context: BuildContext): Widget =
    Container(
      color=Colors.pink600,
      child=Padding(
        padding = EdgeInsets.only(bottom = 2),
        child=SizedBox(
          height = 70,
          child=Container(
            color = Colors.white
          )
        )
      )
    )
}
object NavBar{
  def apply(): NavBar = new NavBar()
}
