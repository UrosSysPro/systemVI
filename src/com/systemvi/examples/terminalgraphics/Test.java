package com.systemvi.examples.terminalgraphics;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

import java.io.IOException;

public class Test {

    public Screen screen;
    public TextGraphics graphics;
    public int x=10,y=10;

    public Test() throws Exception {
        Terminal terminal=new DefaultTerminalFactory().createTerminal();
        terminal.setCursorVisible(false);
        screen=new TerminalScreen(terminal);
        graphics=screen.newTextGraphics();
        graphics.setBackgroundColor(TextColor.ANSI.BLACK);
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    public void run() throws  Exception {
        screen.startScreen();
        boolean running=true;
        while (running) {
            screen.doResizeIfNecessary();
            long startTime=System.nanoTime();
            screen.clear();
            //input
            int deltaX=0,deltaY=0;
            KeyStroke key;
            while((key=screen.pollInput())!=null){
                if(key.getKeyType()==KeyType.Escape)running=false;
                if(key.getKeyType()==KeyType.ArrowRight)deltaX++;
                if(key.getKeyType()==KeyType.ArrowLeft)deltaX--;
                if(key.getKeyType()==KeyType.ArrowDown)deltaY++;
                if(key.getKeyType()==KeyType.ArrowUp)deltaY--;
            }
            //update
            x+=deltaX;
            y+=deltaY;
            //draw
            for(int i=0;i<20;i++){
                for(int j=0;j<10;j++){
                    graphics.setForegroundColor(new TextColor.RGB(255*i/2/10,255*j/10,128));
                    graphics.setCharacter(i+x,j+y,'#');
                }
            }
            //calculate fps
            long endTime=System.nanoTime();
            long frameTime=endTime-startTime;
            double frameTimeMilliseconds=((double)frameTime)/1000000;
            int fps=(int)(1000d/frameTimeMilliseconds);
            graphics.setBackgroundColor(TextColor.ANSI.BLACK);
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(0,0,"frame time: "+frameTimeMilliseconds+"ms");
            graphics.putString(0,1,"fps: "+fps);

            screen.refresh();
            Thread.sleep(16);
        }
        screen.stopScreen();
    }
    public static void test(){
        try {
            Test test=new Test();
            test.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
