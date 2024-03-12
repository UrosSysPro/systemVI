package com.systemvi.engine.ui.utils.data

import com.systemvi.engine.ui.utils.data

object Alignment extends Enumeration {
  type Alignment=Value
  val topLeft:Alignment=Value(0,"topLeft")
  val topRight:Alignment=Value(1,"topRight")
  val topCenter:Alignment=Value(2,"topCenter")
  val centerLeft:Alignment=Value(3,"centerLeft")
  val centerRight:Alignment=Value(4,"centerRight")
  val center:Alignment=Value(5,"center")
  val bottomLeft:Alignment=Value(6,"bottomLeft")
  val bottomRight:Alignment=Value(7,"bottomRight")
  val bottomCetner:Alignment=Value(8,"bottomCenter")
}
object MainAxisAlignment extends Enumeration {
  type MainAxisAlignment=Value
  val start:MainAxisAlignment =Value(0,"start")
  val end:MainAxisAlignment=Value(1,"end")
  val spaceBetween:MainAxisAlignment=Value(2,"spaceBetween")
  val spaceAround:MainAxisAlignment=Value(3,"spaceAround")
  val center:MainAxisAlignment=Value(4,"center")
}
object CrossAxisAlignment extends Enumeration {
  type CrossAxisAlignment=Value
  val start:CrossAxisAlignment=Value(0,"start")
  val end:CrossAxisAlignment=Value(1,"end")
  val center:CrossAxisAlignment=Value(2,"center")
}
object AxisSize extends Enumeration{
  type AxisSize=Value
  val expand:AxisSize=Value(0,"expand")
  val fit:AxisSize=Value(1,"fit")
}