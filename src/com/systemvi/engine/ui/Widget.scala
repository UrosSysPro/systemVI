package com.systemvi.engine.ui

//  ui=build(state)

abstract class Widget {


  def build():Unit;
  def calculateSize():Unit;
  def calculatePosition():Unit;
  def draw():Unit;
}
