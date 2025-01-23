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

//  val columns=Array(10,16,14,15,18,19)
//  val rows=Array(9,8,7)
  val columns=Array(9,8,7)
  val rows=Array(10,16,14,15,18,19)

  var device:FirmataDevice=null

  var rowPins=Array.empty[Pin]
  var columnPins=Array.empty[Pin]

  var renderer:ShapeRenderer2=null
  var camera:Camera3=null

  val keys:Array[Array[Boolean]]=Array.ofDim(rows.length,columns.length)

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

    device = FirmataDevice("COM9")
    println("starting...")
    device.start()
    device.ensureInitializationIsDone()
    println("device ready to use")

    columnPins=columns.map{i=>
      val pin=device.getPin(i)
      pin.setMode(Mode.OUTPUT)
      pin.setValue(0)
      pin
    }
    rowPins=rows.map{i=>
      val pin=device.getPin(i)
      pin.setMode(Mode.PULLUP)
      pin
    }
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.black)

    columnPins.foreach(pin=>pin.setValue(1))

    columnPins.zipWithIndex.foreach((columnPin,columnIndex)=>{
      columnPin.setValue(0)
      Thread.sleep(1)
      rowPins.zipWithIndex.foreach((rowPin,rowIndex)=>{
        keys(rowIndex)(columnIndex)=rowPin.getValue==1
      })
      columnPin.setValue(1)
      Thread.sleep(1)
    })

    for(i <- keys.indices){
      for(j <- keys(0).indices){
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
