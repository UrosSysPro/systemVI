package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import org.joml.{Vector2f, Vector4f}

class ProgressBar(val value:Float) extends StatelessWidget {
  override def build(context: BuildContext): Widget = {
    SizedBox(
      size = new Vector2f(100,5),
      child = SizedBox(
        size=new Vector2f(100*value,5),
        child = Container(color = new Vector4f(0.2f,0.4f,0.9f,1.0f))
      )
    )
  }
}

object ProgressBar{
  def apply(value: Float): ProgressBar = new ProgressBar(value)
}
