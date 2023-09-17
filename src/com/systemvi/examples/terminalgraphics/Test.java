package com.systemvi.examples.terminalgraphics;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Test {

    public Screen screen;
    public Test()  {
        try{
            screen=new DefaultTerminalFactory().createScreen();
        }catch (Exception e){

        }
    }

    public void run(){
        try{
            screen.startScreen();
            while (true){
                screen.clear();
                screen.setCharacter(20,20,new TextCharacter('@'));
                if(screen.readInput().getKeyType()== KeyType.Escape){
                    break;
                }
                screen.refresh();
                Thread.sleep(16);
            }
            screen.stopScreen();
        }catch (Exception e){

        }
    }

}
