package com.systemvi.engine.ui.utils.data

object Alignment extends Enumeration {
  type Alignment=Value
  val topLeft=Value(0,"topLeft")
  val topRight=Value(1,"topRight")
  val topCenter=Value(2,"topCenter")
  val centerLeft=Value(3,"centerLeft")
  val centerRight=Value(4,"centerRight")
  val center=Value(5,"center")
  val bottomLeft=Value(6,"bottomLeft")
  val bottomRight=Value(7,"bottomRight")
  val bottomCetner=Value(8,"bottomCenter")
}
object MainAxisAlignment extends Enumeration {
  type MainAxisAlignment=Value
  val start=Value(0,"start")
  val end=Value(1,"end")
  val spaceBetween=Value(2,"spaceBetween")
  val spaceAround=Value(3,"spaceAround")
  val center=Value(4,"center")
}
object CrossAxisAlignment extends Enumeration {
  type CrossAxisAlignment=Value
  val start=Value(0,"start")
  val end=Value(1,"end")
  val center=Value(2,"center")
}
object AxisSize extends Enumeration{
  type AxisSize=Value
  val expand=Value(0,"expand")
  val fit=Value(1,"fit")
}