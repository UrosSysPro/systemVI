package com.systemvi.examples.applicationtest;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.window.Window;

public class GameTest extends Game {
    public GameTest(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    @Override
    public void setup(Window window) {

    }


    @Override
    public void loop(float delta) {

    }

    @Override
    public boolean mouseDown(int button, int mods, double x, double y) {
        System.out.println("mouse down");
        return true;
    }
}
