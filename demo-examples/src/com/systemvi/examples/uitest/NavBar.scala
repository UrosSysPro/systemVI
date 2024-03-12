package com.systemvi.examples.uitest

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.{AxisSize, Colors, MainAxisAlignment}
import com.systemvi.engine.ui.widgets.{Container, EdgeInsets, Expanded, Padding, Row, SizedBox, StatelessWidget, Text}
import org.joml.Vector2f

class NavBar extends StatelessWidget{
  override def build(context: BuildContext): Widget =
    Container(
      color=Colors.gray400,
      child=Padding(
        padding = EdgeInsets.only(bottom = 2),
        child=SizedBox(
          height = 70,
          child=Container(
            color = Colors.white,
            child = Row(
              mainAxisAlignment = MainAxisAlignment.spaceBetween,
              children = Array(
                Row(
                  mainAxisSize = AxisSize.fit,
                  children = Array(
                    Text("Prvi link"),
                    Text("Drugi link"),
                    Text("Treci link"),
                    Text("Cetvrti link")
                  )
                ),
                Text("Actions")
              )
            )
          )
        )
      )
    )
}
object NavBar{
  def apply(): NavBar = new NavBar()
}
