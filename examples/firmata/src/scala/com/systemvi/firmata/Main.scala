package com.systemvi.firmata

import cats.effect.{IO, IOApp}
import jssc.SerialPortList
import org.firmata4j.firmata.FirmataDevice

import scala.concurrent.duration.*


object Main {
  def main(args: Array[String]): Unit = {
    SerialPortList.getPortNames().foreach(value=>println(value))
  }
}
