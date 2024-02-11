package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget

class Builder(builder:()=>Widget) extends StatelessWidget {
  override def build(): Widget = builder()
  override def debugPrint(tabs: String): Unit = {
    println(s"$tabs Builder size: ${size.x} ${size.y} position: ${position.x} ${position.y}")
    super.debugPrint(tabs)
  }
}

object Builder{
  def apply(build: () => Widget=()=>null): Builder = new Builder(build)
}
