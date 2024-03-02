package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget

class Builder(builder:()=>Widget) extends StatelessWidget {
  override def build(): Widget = builder()
}

object Builder{
  def apply(build: () => Widget=()=>null): Builder = new Builder(build)
}
