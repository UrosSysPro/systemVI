package com.systemvi.ray_marching.keyboard

import com.systemvi.ray_marching.sdf.{Box, Difference, SDF, Union}
import org.joml.{Vector2f, Vector2i, Vector3f}

class KeyboardToSDF(
                     val oneUSize: Float = 19.0f,
                     val switchSize: Float = 14.0f
                   ) {

  private def keyboardSize(keyboard: Keyboard) = keyboard.gridKeycaps.foldLeft(Vector2f()){(size,row)=>
    val rowWidth = row.foldLeft(0f){ (acc,keycap) =>
      acc + oneUSize * keycap.width.value
    }
    val maxWidth = size.x
    val maxHeight = size.y

    Vector2f(Math.max(rowWidth,maxWidth),maxHeight + oneUSize)
  }

  private def bottomCase(size: Vector2i) = {

  }

  private def sidePanel(size: Vector2i) = {

  }

  def toSDF(keyboard: Keyboard): SDF = {

    val size = keyboardSize(keyboard)

    var x: Float = 0
    var y: Float = 0

    val switchSdfs = for{
      (row,rowIndex) <- keyboard.gridKeycaps.zipWithIndex
      (keycap,columnIndex) <- row.zipWithIndex
    } yield {
      val switchSdf = Box(Vector3f(switchSize / 2))
        .translate(Vector3f(
          x + oneUSize * keycap.width.value/2  - size.x/2,
          y + oneUSize * keycap.height.value/2 - size.y/2,
          0
        ))
      x += oneUSize*keycap.width.value
      if(columnIndex==row.length-1){
        x = 0
        y += oneUSize
      }
      switchSdf
    }

    val topPlatePadding = 10

    val topPlateSdf = Box(Vector3f(
      size.x / 2 + topPlatePadding,
      size.y / 2 + topPlatePadding,
      2
    ))

    new Difference(
      Union(switchSdfs),
      topPlateSdf,
    )
  }
}
