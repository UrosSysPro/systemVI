package com.systemvi.engine.window;

public class InputAdapter implements InputProcessor{

    @Override
    public boolean keyDown(int key, int scancode, int mods) {
        return false;
    }

    @Override
    public boolean keyUp(int key, int scancode, int mods) {
        return false;
    }

    @Override
    public boolean mouseDown(int button, int mods, double x, double y) {
        return false;
    }

    @Override
    public boolean mouseUp(int button, int mods, double x, double y) {
        return false;
    }

    @Override
    public boolean mouseMove(double x, double y) {
        return false;
    }

    @Override
    public boolean scroll(double offsetX, double offsetY) {
        return false;
    }

    @Override
    public boolean resize(int width, int height) {
        return false;
    }
}
