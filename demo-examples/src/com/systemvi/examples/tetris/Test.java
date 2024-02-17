package com.systemvi.examples.tetris;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.systemvi.engine.application.Application;

public class Test extends Application {
    public Test(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    public Screen screen;
    public TextGraphics graphics;
    public Tetris tetris;
    @Override
    public void setup() {
        try{
            Terminal terminal=new DefaultTerminalFactory().createTerminalEmulator();
            screen=new TerminalScreen(terminal);
            screen.startScreen();
            graphics=screen.newTextGraphics();
            tetris=new Tetris(30,15);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void loop(float delta) {
        try{
            screen.doResizeIfNecessary();
            screen.clear();

            tetris.input(screen);
            tetris.update();
            tetris.draw(graphics);

            screen.refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
