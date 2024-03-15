package com.systemvi.engine.window;

public class InputMultiplexer implements InputProcessor{
    private final InputProcessor[] processors;

    public InputMultiplexer(InputProcessor... processors){
        this.processors=processors;
    }
    @Override
    public boolean keyDown(int key, int scancode, int mods) {
        for(int i=processors.length-1;i>=0;i--){
            if(processors[i].keyDown(key,scancode,mods))return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int key, int scancode, int mods) {
        for(int i=processors.length-1;i>=0;i--){
            if(processors[i].keyUp(key,scancode,mods))return true;
        }
        return false;
    }

    @Override
    public boolean mouseDown(int button, int mods, double x, double y) {
        for(int i=processors.length-1;i>=0;i--){
            if(processors[i].mouseDown(button,mods,x,y))return true;
        }
        return false;
    }

    @Override
    public boolean mouseUp(int button, int mods, double x, double y) {
        for(int i=processors.length-1;i>=0;i--){
            if(processors[i].mouseUp(button,mods,x,y))return true;
        }
        return false;
    }

    @Override
    public boolean mouseMove(double x, double y) {
        for(int i=processors.length-1;i>=0;i--){
            if(processors[i].mouseMove(x,y))return true;
        }
        return false;
    }

    @Override
    public boolean scroll(double offsetX, double offsetY) {
        for(int i=processors.length-1;i>=0;i--){
            if(processors[i].scroll(offsetX,offsetY))return true;
        }
        return false;
    }

    @Override
    public boolean resize(int width, int height) {
        for(int i=processors.length-1;i>=0;i--){
            if(processors[i].resize(width,height))return true;
        }
        return false;
    }
}
