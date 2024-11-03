package com.systemvi.warriors

import com.systemvi.warriors.characters.Despot

object Main {
  def main(args: Array[String]): Unit = {
    val game=Game()
    game.setup()
    while(true){
      game.loop()
      Thread.sleep(16)
    }
  }
}
