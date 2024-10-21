package com.systemvi.scala


//interface se zove trait
trait Init{
  def init(): Unit
}
trait Disposable {
  def dispose():Unit
}
trait Drawable{
  def draw():Unit
}

//za prvi trait se pise extends, za svaki sledeci with

class Test extends Disposable with Drawable with Init{

  //posto je celo telo klase konstruktor ovde mosemo da pozovemo init
  init()
  
  override def init(): Unit = {
    
  }
  override def draw(): Unit = {
    
  }
  override def dispose(): Unit = {
    
  }
}
