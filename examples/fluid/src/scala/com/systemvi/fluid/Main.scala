package com.systemvi.fluid

import com.systemvi.fluid.cpu.Fluid
import com.systemvi.fluid.gpu.App

object Main{
  def main(args: Array[String]): Unit = {
    new App().run()
//    new Fluid(3,3,60).run()
  }
}