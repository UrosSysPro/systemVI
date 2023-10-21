package com.systemvi.examples.fluid;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.window.Window;
import static org.lwjgl.opengl.GL33.*;

public class Fluid extends Application {

    public Fluid(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    Window window;

    @Override
    public void setup() {
        window=new Window(500,500,"Fluid Sim");
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
