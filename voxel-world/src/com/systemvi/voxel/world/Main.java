package com.systemvi.voxel.world;
import com.systemvi.engine.application.Game;
import com.systemvi.engine.window.Window;

public class Main extends Game{
    public Main(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    @Override
    public void setup(Window window) {

    }

    @Override
    public void loop(float delta) {

    }
    public static void main(String[] args) {
        new Main(3,3,60,800,600,"Voxel World").run();
    }
}
