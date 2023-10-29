package com.systemvi.examples.inversekinematics;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.window.Window;
import com.systemvi.examples.datastructures.ArrayList;

import static org.lwjgl.opengl.GL33.*;

public class Fabrik extends Application {

    public Fabrik(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }
    public Window window;
    public ArrayList<Vector> points;
    public ArrayList<Float> lengths;

    @Override
    public void setup() {
        window=new Window(800,600,"Fabrik");
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();
        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);



        window.swapBuffers();
    }
}
