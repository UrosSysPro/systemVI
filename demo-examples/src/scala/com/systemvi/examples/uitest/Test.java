package com.systemvi.examples.uitest;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.ui.WidgetRenderer;
import com.systemvi.engine.window.Window;
import org.joml.Vector4f;

public class Test extends Game {
    public Test(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }
    WidgetRenderer renderer;
    @Override
    public void setup(Window window) {
        renderer=new WidgetRenderer(window);
    }

    @Override
    public void loop(float delta) {
        renderer.rect(400,300,200,300,new Vector4f(0.3f,0.6f,0.9f,1.0f));
        renderer.rect(100,100,150,200,new Vector4f(0.6f,0.2f,0.9f,1.0f));
        renderer.flush();
    }
}
