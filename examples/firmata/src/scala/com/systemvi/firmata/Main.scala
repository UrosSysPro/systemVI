package com.systemvi.firmata

import cats.effect.{IO, IOApp, Temporal}
import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.renderers.{ShapeRenderer2, Square}
import com.systemvi.engine.texture.{Format, Texture}
import com.systemvi.engine.texture.Texture.Repeat
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.window.Window
import jssc.SerialPortList
import org.firmata4j.Pin
import org.firmata4j.Pin.Mode
import org.firmata4j.firmata.FirmataDevice

import scala.concurrent.duration.*


object Main extends Game(3,3,60,800,600,"firmata"){

  val col0=2
  val col1=3
  val col2=4
  val row0=5
  val row1=6
  val row2=7

//  val col0 = 5
//  val col1 = 6
//  val col2 = 7
//  val row0 = 2
//  val row1 = 3
//  val row2 = 4

  var device:FirmataDevice=null

  var pinCol0:Pin=null
  var pinCol1:Pin=null
  var pinCol2:Pin=null
  var pinRow0:Pin=null
  var pinRow1:Pin=null
  var pinRow2:Pin=null

  var renderer:ShapeRenderer2=null
  var camera:Camera3=null

  val keys:Array[Array[Boolean]]=Array.ofDim(3,3)

  override def setup(window: Window): Unit = {

    renderer=ShapeRenderer2()
    renderer.texture = Texture.builder()
      .format(Format.RGB)
      .width(100)
      .height(100)
      .borderColor(Colors.white)
      .verticalRepeat(Repeat.CLAMP_BORDER)
      .horizontalRepeat(Repeat.CLAMP_BORDER)
      .build()
    camera=Camera3.builder2d()
      .size(window.getWidth.toFloat,window.getHeight.toFloat)
      .position(window.getWidth.toFloat/2,window.getHeight.toFloat/2)
      .build()

    renderer.view(camera.view)
    renderer.projection(camera.projection)

    SerialPortList.getPortNames().foreach(value => println(value))

    device = FirmataDevice("COM5")
    println("starting...")
    device.start()
    device.ensureInitializationIsDone()
    println("device ready to use")

    pinCol0 = device.getPin(col0)
    pinCol1 = device.getPin(col1)
    pinCol2 = device.getPin(col2)

    pinCol0.setMode(Mode.OUTPUT)
    pinCol0.setValue(0)
    pinCol1.setMode(Mode.OUTPUT)
    pinCol1.setValue(0)
    pinCol2.setMode(Mode.OUTPUT)
    pinCol2.setValue(0)

    pinRow0 = device.getPin(row0)
    pinRow1 = device.getPin(row1)
    pinRow2 = device.getPin(row2)
    pinRow0.setMode(Mode.PULLUP)
    pinRow1.setMode(Mode.PULLUP)
    pinRow2.setMode(Mode.PULLUP)
  }

  override def loop(delta: Float): Unit = {

    Utils.clear(Colors.black)

//    renderer.draw(Square(0,0,50,if pinRow0.getValue==0 then Colors.red400 else Colors.green400))
//    renderer.draw(Square(50,0,50,if pinRow1.getValue==0 then Colors.red400 else Colors.green400))
//    renderer.draw(Square(100,0,50,if pinRow2.getValue==0 then Colors.red400 else Colors.green400))
//    renderer.flush()

    pinCol0.setValue(1)
    pinCol1.setValue(1)
    pinCol2.setValue(1)

    pinCol0.setValue(0)
    Thread.sleep(1)
    keys(0)(0)=pinRow0.getValue==1
    keys(1)(0)=pinRow1.getValue==1
    keys(2)(0)=pinRow2.getValue==1
    pinCol0.setValue(1)
    Thread.sleep(1)

    pinCol1.setValue(0)
    Thread.sleep(1)
    keys(0)(1) = pinRow0.getValue == 1
    keys(1)(1) = pinRow1.getValue == 1
    keys(2)(1) = pinRow2.getValue == 1
    pinCol1.setValue(1)
    Thread.sleep(1)

    pinCol2.setValue(0)
    Thread.sleep(1)
    keys(0)(2) = pinRow0.getValue == 1
    keys(1)(2) = pinRow1.getValue == 1
    keys(2)(2) = pinRow2.getValue == 1
    pinCol2.setValue(1)
    Thread.sleep(1)

    for(i <- 0 until 3){
      for(j <- 0 until 3){
        renderer.draw(Square(i*50f,j*50f,50,if keys(i)(j) then Colors.red400 else Colors.green400))
      }
    }
    renderer.flush()
  }

  override def dispose(): Unit = {
    println("stoping...")
    device.stop()
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}
