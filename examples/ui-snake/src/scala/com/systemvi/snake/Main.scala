package com.systemvi.snake

import com.systemvi.engine.ui.runApp

object Main {
  def main(args: Array[String]): Unit = {
    runApp(
      width=550,
      height=600,
      title="Snake",
      home=Game()
    )
  }
}