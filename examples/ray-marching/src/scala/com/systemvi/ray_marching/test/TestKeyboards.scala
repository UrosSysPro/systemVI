package com.systemvi.ray_marching.test

import com.systemvi.ray_marching.keyboard.{GridKeycap, Keyboard, KeycapSize}

object TestKeyboards {
  private def toKeyboard(keyboardString: String): Keyboard = {
    val rowsString = keyboardString.trim.split("\n").map(_.trim).filter(_.nonEmpty)
    val keysString = rowsString.map{ rowString =>
      rowString.split(" ").map(_.trim).filter(_.nonEmpty)
    }
    val gridKeycaps = keysString.map{ row =>
      row.map{ keycapString =>
        keycapString.split('|').toList match {
          case List(label,widthString,heightString) =>
            val width = KeycapSize.fromOrdinal(widthString.toInt)
            val height = KeycapSize.fromOrdinal(heightString.toInt)
            GridKeycap(label,List.empty,width, height)
          case _ => throw new Exception(s"wrong keycap format $keycapString")
        }
      }.toList
    }.toList
    Keyboard(gridKeycaps,List.empty)
  }

  val keyboard60: Keyboard = toKeyboard(
    s"""
       |esc|0|0  1|0|0 2|0|0 3|0|0 4|0|0  5|0|0 6|0|0 7|0|0 8|0|0 9|0|0 -|0|0 =|0|0 backspace|4|0
       |tab|2|0  q|0|0 w|0|0 e|0|0 4|0|0  r|0|0 t|0|0 y|0|0 u|0|0 i|0|0 o|0|0 p|0|0 [|0|0 ]|0|0 \\|3|0
       |caps|3|0  a|0|0 s|0|0 d|0|0 f|0|0  g|0|0 h|0|0 j|0|0 k|0|0 l|0|0 ;|0|0 '|0|0 enter|5|0
       |shift|5|0  z|0|0 x|0|0 c|0|0 v|0|0  b|0|0 n|0|0 m|0|0 ,|0|0 .|0|0 /|0|0 shift|6|0
       |ctrl|1|0  win|1|0 alt|1|0 space|7|0 alt|1|0  win|1|0 context|1|0 ctrl|1|0
       |""".stripMargin)
}
