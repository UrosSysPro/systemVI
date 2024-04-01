package com.systemvi.examples.uitest

import com.systemvi.engine.application.Game
import com.systemvi.engine.ui.{Scene, Widget}
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.{Alignment, BoxDecoration, Colors}
import com.systemvi.engine.ui.widgets.{Align, Container, SizedBox, StatelessWidget}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window

class ContainerTest extends Game(3,3,60,800,600,"Container Test"){
  var scene:Scene=null
  override def setup(window: Window): Unit = {
    scene=new Scene(
      root=ContainerTestWidget(),
      window=window
    )
    setInputProcessor(scene)
  }
  override def loop(delta: Float): Unit = {
    Utils.clear(0,0,0,0,Buffer.COLOR_BUFFER)
    scene.resize(scene.width,scene.height)
    scene.animate(delta)
    scene.draw()
  }
}


class ContainerTestWidget extends StatelessWidget{
  override def build(context: BuildContext): Widget = {
    Align(
      alignment=Alignment.bottomLeft,
      child=Container(
        decoration=BoxDecoration(color=Colors.red500,borderRadius=20),
        child = SizedBox(width = 100,height = 100)
      )
    )
  }
}
object ContainerTestWidget {
  def apply(): ContainerTestWidget=new ContainerTestWidget()
}
