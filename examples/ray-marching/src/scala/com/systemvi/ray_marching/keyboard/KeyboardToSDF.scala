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
                     val slotWidth: Float = 10.0f,
                     val frameHeight: Float = 20f,
                     val borderWidth: Float = 4f,
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

  private def bottomCase(keypadSize: Vector2f): SDF = {
    val bottomCase = {
      Box(Vector3f(
        keypadSize.x+sidePanelWidth/2,
        keypadSize.y+sidePanelWidth/2,
        frameHeight/2f,
      ).mul(0.5f))
    }

    val keypadCutter = {
      Box(Vector3f(
        keypadSize.x,
        keypadSize.y,
        frameHeight/2f,
      ).mul(0.5f))
        .translate(Vector3f(
          0,0,4
        ))
    }

    val keypadEarMiddleTabs = {
      Box(Vector3f())
    }

    new Difference(
      keypadCutter,
      Union(List(bottomCase, keypadEarMiddleTabs)),
    )
  }

  private def sidePanel(keypadSize: Vector2f) = {

    val frame = {
      Box(Vector3f(
        keypadSize.x + borderWidth * 2f,
        keypadSize.y + borderWidth * 2f,
        frameHeight
      ).mul(0.5f))
    }

    val cutter = {

      val keypadBox = Box(Vector3f(
        keypadSize.x,
        keypadSize.y,
        frameHeight * 2
      ).mul(0.5f))

      val keypadEarsBox = Box(Vector3f(
        keypadSize.x + sidePanelWidth / 2f,
        keypadSize.y + sidePanelWidth / 2f,
        frameHeight
      ).mul(0.5f))
        .translate(Vector3f(
          0, 0, -(frameHeight / 2f - topPlateHeight / 2f)
        ))

      Union(
        List(keypadBox, keypadEarsBox)
      )
    }

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
    val switchSdfs = {
      var x: Float = 0
      var y: Float = 0

      for{
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
    }

//    top plate rectangle
    val topPlateSdf = {
      Box(Vector3f(
        keypadSize.x,
        keypadSize.y,
        topPlateHeight
      ).mul(0.5f))
    }

//    side ears
    val sideEarsSdfs = {
      val sideEarGap = 10f
      val sideEarWidth = sidePanelWidth / 2f
      val sideEarHeight = topPlateHeight / 2f

      val leftRightEarSdf: Box = Box(Vector3f(sideEarWidth, (keypadSize.y - sideEarGap) / 2f, sideEarHeight).mul(0.5f))
      val bottomEarSdf: Box = Box(Vector3f((keypadSize.x - sideEarGap * 2f) / 3f, sideEarWidth, sideEarHeight).mul(0.5f))

      val leftRightEarsSdf = for (i <- List(-1, 1); j <- List(-1, 1)) yield {
        leftRightEarSdf.translate(Vector3f(
          (keypadSize.x + leftRightEarSdf.halfSize.x * 2f) / 2f * i,
          (keypadSize.y - leftRightEarSdf.halfSize.y * 2f) / 2f * j,
          topPlateSdf.halfSize.z - leftRightEarSdf.halfSize.z,
        ))
      }

      val bottomEarsSdf = for(i <- List(-1, 0, 1); j <- List(1)) yield {
        bottomEarSdf.translate(Vector3f(
          (keypadSize.x - bottomEarSdf.halfSize.x * 2f) / 2f * i,
          (keypadSize.y + bottomEarSdf.halfSize.y * 2f) / 2f * j,
          topPlateSdf.halfSize.z - bottomEarSdf.halfSize.z,
        ))
      }

      leftRightEarsSdf ++ bottomEarsSdf
    }

//    final result
    new Difference(
      Union(switchSdfs),
      Union(sideEarsSdfs :+ topPlateSdf),
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
