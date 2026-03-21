package com.systemvi.ray_marching.keyboard

import org.joml.Vector2f

sealed trait Key

case class NormalKey(value: Char) extends Key

enum MacroAction(value:Char) {
  case Press(value:Char) extends MacroAction(value)
  case Release(value:Char) extends MacroAction(value)
}

case class MacroKey(name: String, keys: List[MacroAction]) extends Key

case class LayerKey(layer: Int) extends Key

case class TapDanceKey(layer: Int) extends Key

enum KeycapSize(val value: Float) {
  case Size100U extends KeycapSize(1.0f)  // 0
  case Size125U extends KeycapSize(1.25f) // 1
  case Size150U extends KeycapSize(1.5f)  // 2
  case Size175U extends KeycapSize(1.75f) // 3

  case Size200U extends KeycapSize(2.0f)  // 4
  case Size225U extends KeycapSize(2.25f) // 5
  case Size275U extends KeycapSize(2.75f) // 6

  case Size625U extends KeycapSize(6.25f)  // 7

  case SizeCustom(override val value: Float) extends KeycapSize(value)
}

case class GridKeycap(
                       label: String,
                       keys: List[Key],
                       width: KeycapSize,
                       height: KeycapSize
                     )

case class FreeFormKeycap(
                           label: String,
                           keys: List[Key],
                           width: KeycapSize,
                           height: KeycapSize,
                           position: Vector2f
                         )

case class Keyboard(
                     gridKeycaps: List[List[GridKeycap]],
                     freeFormKeycaps: List[FreeFormKeycap]
                   )