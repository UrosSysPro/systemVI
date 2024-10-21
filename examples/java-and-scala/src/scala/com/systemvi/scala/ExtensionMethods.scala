package com.systemvi.scala

import org.joml.Vector2f

//mogu da definisem skraceno ime za postojeci tip
type vec2=Vector2f

//definisem blok sa funkcijama koje dodam na postojeci tip
extension(v:vec2){
  def +(v1:Vector2f): vec2 = new vec2(v).add(v1)
  def *(v1:Vector2f): Float = v1.dot(v)
}

object ExtensionMethods{
  def main(args: Array[String]): Unit = {
    val a=new vec2(1,1)
    val b=new vec2(1,1)
    //totalno nova sintaksa za tip koji nismo mi pisali
    //u vec postojecu klasu smo dodali novu funkcionalnost
    val v=a+b
    val dot=a*b
  }
}
