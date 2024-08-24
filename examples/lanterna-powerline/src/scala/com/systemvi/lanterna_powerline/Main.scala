package com.systemvi.lanterna_powerline

import com.googlecode.lanterna.{TerminalPosition, TerminalSize, TextCharacter}
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame

import java.awt.{Font, GraphicsEnvironment}
import scala.util.control.Breaks._

object Main{
  def main(args: Array[String]): Unit = {

    val testString="- --- == === != !== =!= =:= =/= <= >>> << >> || -> // /// /* */ /= //= /== @_ __ ??? <:< ;;;";
    val testString2=" ╵ ╶ ╷ ╸ ╹werline\uE0A0 \uE0A1 \uE0A2 \uE0B0 \uE0B1 \uE0B2 \uE0B3"
    val customFont = new Font("JetBrains Mono", Font.PLAIN, 13)
    println(customFont.getFontName)
    println(customFont.canDisplay('a'))
    println(customFont.getFamily)
    println(customFont.isPlain)

    val terminal=new DefaultTerminalFactory().createSwingTerminal()
    terminal.setFont(customFont)
    terminal.setVisible(true)
    terminal.invalidate()
    terminal.repaint()

    val screen = new TerminalScreen(terminal)
    screen.startScreen()

    val graphics=screen.newTextGraphics()

    breakable{
      while(true){
        val input=screen.pollInput()
        if(input!=null){
          if(input.getCharacter=='q')break()
        }

        screen.clear()
        graphics.putString(new TerminalPosition(0,0),"hello world")
        graphics.putString(new TerminalPosition(0,1),testString)
        graphics.putString(new TerminalPosition(0,10),testString2)
        screen.refresh()
        Thread.sleep(16)
      }
    }

    screen.stopScreen()
  }
}