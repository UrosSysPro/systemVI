package com.systemvi.engine.application;

import com.systemvi.engine.window.InputProcessor;
import com.systemvi.engine.window.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public abstract class Game implements InputProcessor {
    private final long nanosInSecond,millisInSecond,microsInSecond;
    private final int maxNanos;
    private boolean exit=false;
    private double lastFrame,frameTime;
    private int targetFPS;
    private double targetFrameTime;
    private Window window;
    public Game(int openglVersionMajor,int openglVersionMinor,int targetFPS,int windowWidth,int windowHeight,String title){
        nanosInSecond=1000_000_000L;
        microsInSecond=1000_000L;
        millisInSecond=1000L;
        maxNanos=1000_000;
        this.targetFPS=targetFPS;
        targetFrameTime=1d/targetFPS;
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,openglVersionMajor);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,openglVersionMinor);
        glfwWindowHint(GLFW_OPENGL_PROFILE,GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER,GLFW_FALSE);

//        glfwWindowHint(GLFW_SAMPLES, 10);

        window=new Window(windowWidth,windowHeight,title);

//        glEnable(GL_MULTISAMPLE);

        window.setInputProcessor(this);
    }

    public void run(){
        setup(window);
        lastFrame=glfwGetTime()-targetFrameTime;
        while(!exit){
            double startTime=glfwGetTime();
            window.pollEvents();
            if(window.shouldClose())exit=true;
            loop((float)(startTime-lastFrame));
            double endTime=glfwGetTime();
            frameTime=endTime-startTime;
            window.swapBuffers();
            sleep(targetFrameTime-frameTime);

            lastFrame=startTime;
        }
        dispose();
    }

    public double getFrameTime() {
        return frameTime;
    }

    public Window getWindow() {
        return window;
    }

    public int getFPS(){
        if(frameTime==0)return (int)nanosInSecond;
        return (int)(1/frameTime);
    }
    public double frameToFrameTime(){
        return glfwGetTime()-lastFrame;
    }

    public int getTargetFPS(){
        return targetFPS;
    }
    public void setTargetFPS(int fps){
        this.targetFPS=fps;
        this.targetFrameTime=1d/fps;
    }
    public void sleep(double time){

        time=time>0?time:0;
        long millis=(long)(time*millisInSecond);
        int nanos=(int)((time*nanosInSecond)%maxNanos);

        try{
            Thread.sleep(millis,nanos);
        }catch (Exception e){
            System.out.println("millis: "+millis);
            System.out.println("nanos: "+nanos);
            e.printStackTrace();
        }
    }

    public  void dispose(){

    }
    public void close(){
        exit=true;
    }

    public abstract void setup(Window window);
    public abstract void loop(float delta);

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
    public void setInputProcessor(InputProcessor processor){
        if(processor==null)
            window.setInputProcessor(this);
        else
            window.setInputProcessor(processor);
    }
}
