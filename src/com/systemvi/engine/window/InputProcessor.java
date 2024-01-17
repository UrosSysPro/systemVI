package com.systemvi.engine.window;

public interface InputProcessor {
    boolean keyDown(int key, int scancode, int mods);
    boolean keyUp(int key, int scancode, int mods);
    boolean mouseDown(int button,int mods,double x,double y);
    boolean mouseUp(int button,int mods,double x,double y);
    boolean mouseMove(double x,double y);
    boolean scroll(double offsetX,double offsetY);
    boolean resize(int width,int height);
}
