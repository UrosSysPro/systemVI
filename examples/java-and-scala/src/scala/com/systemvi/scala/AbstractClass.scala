package com.systemvi.scala

import com.systemvi.engine.application.Game
import com.systemvi.engine.window.Window


//potpis abstraktne klase je default konstruktor
abstract class AbstractClass(val a:Int) {
  def f():Unit
}

//potpis klase je konstruktor, i poziva kontruktor nadklase
class Implementation(val string:String,value:Int) extends AbstractClass(value){
  override def f(): Unit = {
    println("f")
  }
}

//pravi samo jedan objekat koji nasledjuje klasu Game
object C extends Game(3,3,60,800,600,"title"){
  
  //Game je abstraktna klasa
  
  //metode koje imaju ??? kao definicije se kompajliraju, ali ako ih pozovemo u kodu, desi se MethodNotImplementedException
  //korisno za testiranje
  override def setup(window: Window): Unit = ???
  
  override def loop(delta: Float): Unit = ???
}