package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.{Vector2f, Vector4f}

class Switch(val value:Boolean) extends StatelessWidget {

  override def build(): Widget =
    SizedBox(
      size=new Vector2f(55,30)
    )

  override def draw(renderer: WidgetRenderer): Unit = {
    val padding=5f
    var x=position.x;
    var y=position.y;
    val size=this.size.y;
    val color=new Vector4f(0.7f);
    if(value){
      color.set(0.2f,0.7f,0.3f,1.0f)
      x=position.x+this.size.x-size
    }
    renderer.rect(
      position.x,
      position.y,
      this.size.x,
      this.size.y,
      color
    )
    renderer.rect(
      x+padding,y+padding,size-2*padding,size-2*padding,new Vector4f(1.0f)
    )
  }
}

object Switch{
  def apply(value: Boolean): Switch = new Switch(value)
}