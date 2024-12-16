package com.systemvi.firmata

import cats.effect.{IO, IOApp}
import jssc.SerialPortList
import org.firmata4j.firmata.FirmataDevice

import scala.concurrent.duration.*


object Main {
  def main(args: Array[String]): Unit = {
    SerialPortList.getPortNames().foreach(value=>println(value))

    val device=FirmataDevice("COM5")
    println("starting...")
    device.start()
    device.ensureInitializationIsDone()
    println("device ready to use")


    Thread.sleep(10000)

    println("stoping...")
    device.stop()
  }
}
