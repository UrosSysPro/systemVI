package com.systemvi.platformer

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory

import scala.util.control.Breaks

case class Player(var x:Int=0, var y:Int=0)

object Main {
  def main(args: Array[String]): Unit = {
    val terminal=DefaultTerminalFactory().setForceTextTerminal(true).createTerminal()
    val screen=TerminalScreen(terminal)
    screen.startScreen()
    val graphics=screen.newTextGraphics()
    var key='a'
    terminal.setCursorVisible(false)

    val player=Player()

    Breaks.breakable{
      key='#'
      while (true) {
        var input: KeyStroke = null
        Breaks.breakable {
          while (true) {
            input = terminal.pollInput()
            if (input == null) Breaks.break()
            key = input.getCharacter
          }
        }

        if (key == 'q') Breaks.break()
        if(key=='a')player.x-=1
        if(key=='s')player.y+=1
        if(key=='w')player.y-=1
        if(key=='d')player.x+=1

        screen.clear()
        graphics.setForegroundColor(TextColor.ANSI.RED)
        graphics.putString(player.x, player.y, s"$key")
        screen.refresh()
        Thread.sleep(16)
      }
    }
  }
}
