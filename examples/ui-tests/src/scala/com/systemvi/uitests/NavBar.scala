package com.systemvi.uitests

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.{AxisSize, Colors, MainAxisAlignment}
import com.systemvi.engine.ui.widgets.*
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
                    Text("Prvi link",font= WidgetTest.font),
                    Text("Drugi link",font=WidgetTest.font),
                    Text("Treci link",font=WidgetTest.font),
                    Text("Cetvrti link",font=WidgetTest.font)
                  )
                ),
                Text("Actions",font=WidgetTest.font)
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
