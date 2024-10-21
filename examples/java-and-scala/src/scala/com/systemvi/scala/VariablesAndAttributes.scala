package com.systemvi.scala

//case class je klasa koja se definise u jednoj liniji, svi parametri su public atributi, svi imaju getter i opciono setter
case class Point(var x:Float=0,var y:Float=0,size:Float)

//scala ima samo reference, ne postoje primitivni tipovi

//potpis klase je i potpis konstruktora
//parametri oznaceni sa val ili var su atributi klase (value,value3 su atributi, value2 nije atribut)
//sve promenljive definisane u telu klase su takodje atributi (value4,value5) default je public, private mora da se napise
class VariablesAndAttributes(val value:Int=42,value2:Int=42,var value3:Int) {
  val value4:Int=42
  private val value5:Int=2
}

//uz svaku klasu mozemo da definisemo kompanion objekat, sve static metode i atributi idu u kompanion objekat
object VariablesAndAttributes{
  val value6:Int=2  
}
