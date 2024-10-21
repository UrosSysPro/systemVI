package com.systemvi.scala


class ConstructorAndMethods(val value: Int = 0) {
  //Unit je u mesto void
  def f(): Unit = {
    println("hello")
  }

  //ako funkcija ima samo jednu liniju moze da se napise u jednoj liniji bez zagrada
  //poslednja linija funkcije je return value
  def f(a: Int): Int = a

  //normalna funkcija sa vise parametara
  def f(a: Int, b: Int, c: Int): Int = a + b + c

  //multiple paramter list funkcija, poziva se kao f(2)(2), f(2) je sama po sebi funkcija
  def f(a: Int)(b: Int): Int = a + b
}

object ConstructorAndMethods {
  
  //static method
  def f(): Unit = {
    
  }
}
