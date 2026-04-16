package com.systemvi.ray_marching.keyboard

import com.systemvi.ray_marching.sdf.{Box, Difference, SDF, Union}
import org.joml.{Vector2f, Vector2i, Vector3f}

class KeyboardToSDF(
                     val oneUSize: Float = 19.0f,
                     val keycapPadding: Vector2f = Vector2f(1.0f),
                     val switchSize: Float = 14.0f,
                     val sidePanelHeight: Float = 20.0f,
                     val sidePanelWidth: Float = 8.5f,
                     val topPlateHeight: Float = 4.0f,
                     val topPlateTabsHeight: Float = 2.0f,
                     val slotWidth: Float = 10.0f
                   ) {
  private val keycapSizeWithPadding = Vector2f(oneUSize+keycapPadding.x ,oneUSize+keycapPadding.y)

  def keypadSize(keyboard: Keyboard): Vector2f = keyboard.gridKeycaps.foldLeft(Vector2f()){ (size, row)=>
    val rowWidth = row.foldLeft(0f){ (acc,keycap) =>
      acc + keycapSizeWithPadding.x * keycap.width.value
    }
    val maxWidth = size.x
    val maxHeight = size.y

    Vector2f(Math.max(rowWidth,maxWidth),maxHeight + keycapSizeWithPadding.y)
  }

  private def bottomCase(keypadSize: Vector2f) = {
    val sideWidth = 4f
    val sideHeight = 4f
    val plateDepth = 2f

    val plate = Box(Vector3f(keypadSize.x,keypadSize.y,plateDepth).mul(0.5f)).translate(Vector3f(0,0,plateDepth/2))

    val left = Box(Vector3f(sideWidth,keypadSize.y,sideHeight).mul(0.5f)).translate(Vector3f(-keypadSize.x/2+sideWidth/2,0,sideHeight/2))
    val right = Box(Vector3f(sideWidth,keypadSize.y,sideHeight).mul(0.5f)).translate(Vector3f(keypadSize.x/2-sideWidth/2,0,sideHeight/2))

    val top = Box(Vector3f(keypadSize.x,sideWidth,sideHeight).mul(0.5f)).translate(Vector3f(0,keypadSize.y/2-sideWidth/2,sideHeight/2))
    val bottom = Box(Vector3f(keypadSize.x,sideWidth,sideHeight).mul(0.5f)).translate(Vector3f(0,-keypadSize.y/2+sideWidth/2,sideHeight/2))

    Union(List(plate,left,right,top,bottom))
  }

  private def sidePanel(keypadSize: Vector2f) = {
    val frameHeight = 20f
    val borderWidth = 4f
    val frame = Box(Vector3f(keypadSize.x+borderWidth*2,keypadSize.y+borderWidth*2,frameHeight).mul(0.5f))
    val cutter = Box(Vector3f(keypadSize.x,keypadSize.y,frameHeight*2).mul(0.5f))
    Difference(List(cutter,frame))
  }

  private def switchSdf(x:Float,y:Float): SDF = {
    new Union(
      Box(halfSize = Vector3f(switchSize).mul(0.5f)),
      Box(halfSize = Vector3f(switchSize,switchSize+2,switchSize/2).mul(0.5f)).translate(Vector3f(0,0,-switchSize/2).mul(0.5f)),
    ).translate(Vector3f(x,y,0))
  }

  private def topPlate(keyboard: Keyboard, keypadSize: Vector2f) = {

    //    get positions of all switches
    var x: Float = 0
    var y: Float = 0

    val switchSdfs = for{
      (row,rowIndex) <- keyboard.gridKeycaps.zipWithIndex
      (keycap,columnIndex) <- row.zipWithIndex
    } yield {
      val switchSdf = this.switchSdf(
        x + keycapSizeWithPadding.x * keycap.width.value/2  - keypadSize.x/2,
        y + keycapSizeWithPadding.y * keycap.height.value/2 - keypadSize.y/2,
      )
      x += keycapSizeWithPadding.x * keycap.width.value
      if(columnIndex==row.length-1){
        x = 0
        y += keycapSizeWithPadding.y
      }
      switchSdf
    }

//    top plate rectangle
    val topPlateSdf = Box(Vector3f(
      keypadSize.x,
      keypadSize.y,
      topPlateHeight
    ).mul(0.5f))

//    side ears
//    val sideEarGap = 10f
//    
//    val sideEarWidth = sidePanelWidth / 2f
//    val sideEarHeight = (keypadSize.y - sideEarGap) / 2f
//    val sideEarDepth = 2f
//
//    val sideEarSdf = Box(Vector3f(
//      sideEarWidth, sideEarHeight, sideEarDepth
//    ).mul(0.5f))
//
//    val bottomEarWidth = (keypadSize.x - sideEarGap * 2f) / 3f
//    val bottomEarHeight = sidePanelWidth / 2f
//    val bottomEarDepth = 2f
//    
//    val bottomEarSdf = Box(Vector3f(
//      bottomEarWidth, bottomEarHeight, bottomEarDepth,
//    ).mul(0.5f))
//
//    val sidePanelEars = List(
////      left
//      sideEarSdf.translate(Vector3f(-(keypadSize.x+sideEarWidth)/2,keypadSize.y,0).mul(2f)),
//      sideEarSdf.translate(),
////      right
//      sideEarSdf.translate(),
//      sideEarSdf.translate(),
//    )
//    val bottomPanelEars = List(
//      bottomEarSdf.translate(),
//      bottomEarSdf.translate(),
//      bottomEarSdf.translate(),
//    )

//    final result
    new Difference(
      Union(switchSdfs),
      topPlateSdf,
    )
  }

  def toSDF(keyboard: Keyboard): SDF = {
    val size = keypadSize(keyboard)

    val topPlatePart = topPlate(keyboard,size)
    val bottomCasePart = bottomCase(size)
    val sidePanelPart = sidePanel(size)

    Union(List(
      bottomCasePart.translate(Vector3f(0,0,0)),
      sidePanelPart.translate(Vector3f(0,0,40)),
      topPlatePart.translate(Vector3f(0,0,80)),
    ))
  }

  def toSDFParts(keyboard: Keyboard): List[SDF] = {

    val keypadSize = this.keypadSize(keyboard)

    val topPlatePart = topPlate(keyboard,keypadSize)
    val bottomCasePart = bottomCase(keypadSize)
    val sidePanelPart = sidePanel(keypadSize)

    List(
      topPlatePart,
      bottomCasePart,
      sidePanelPart,
    )
  }
}
