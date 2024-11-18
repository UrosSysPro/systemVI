package com.systemvi.snake

import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import com.systemvi.snake

object Main extends com.systemvi.engine.application.Game(3,3,60,800,600,"Polygon Renderer"){

  var game:snake.Game=null

  override def setup(window: Window): Unit = {
    game=Game()
    setInputProcessor(game)
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.black,Buffer.COLOR_BUFFER)
    game.update(delta)
    game.draw()
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}