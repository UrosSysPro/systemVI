package com.systemvi.engine.syntax


object Conversions:
  given int2Float:Conversion[Int,Float] = (x: Int)=> x.toFloat
  