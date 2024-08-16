package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext

class Builder(builder:()=>Widget) extends StatelessWidget {
  override def build(context:BuildContext): Widget = builder()
}

object Builder{
  def apply(build: () => Widget=()=>null): Builder = new Builder(build)
}
