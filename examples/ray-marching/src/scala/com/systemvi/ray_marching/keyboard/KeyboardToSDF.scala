package com.systemvi.ray_marching.keyboard

import com.systemvi.ray_marching.sdf.{Box, SDF, Union}
import org.joml.Vector3f

class KeyboardToSDF(
                     val oneUSize: Float = 19.0f,
                     val switchSize: Float = 14.0f
                   ) {

  def toSDF(keyboard: Keyboard): SDF = {

    var x: Float = 0
    var y: Float = 0

    val switchSdfs = for{
      (row,rowIndex) <- keyboard.gridKeycaps.zipWithIndex
      (keycap,columnIndex) <- row.zipWithIndex
    } yield {
      val switchSdf = Box(Vector3f(switchSize/2))
        .translate(Vector3f(
          x+oneUSize*keycap.width.value/2,
          y+oneUSize*keycap.height.value/2,
          0
        ))
      x += oneUSize*keycap.width.value
      if(columnIndex==row.length-1){
        x = oneUSize/2
        y += oneUSize
      }
      switchSdf
    }

    Union(switchSdfs)
  }
}
