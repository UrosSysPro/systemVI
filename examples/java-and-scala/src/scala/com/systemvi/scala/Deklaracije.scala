package com.systemvi.scala

import com.systemvi.scala.Enum.First

// u programskom jeziku scala mozemo na top levelu da definisemo promenljive, funkcije, clase, objekte, tipove, enum, case class

class Klasa {

}

object Objekat {

}

//var moze da se overriduje, val ne moze, val je konstantno

var variable: String = "value 1"

val value: Int = 2

//def functionName(parameter:ParameterType):RetyrnType={
//      2 //last line of a function is returned, return keyword is optional
// }
def function(parameter1: Int, parameter2: Float): Unit = {

}

enum Enum {
  case First
  case Second
  case Third
}

//type sluzi tome da preimenujemo jedan tip
type DrugoImeZaEnum = Enum
val primer: DrugoImeZaEnum = First
val primer2: Enum = First

type FunkcijaKojaPrimaInt = Int => Unit
val f: FunkcijaKojaPrimaInt = (value: Int) => println(value)
val g: Int => Unit = (value: Int) => println(value)
