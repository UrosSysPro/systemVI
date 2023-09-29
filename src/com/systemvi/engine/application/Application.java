package com.systemvi.engine.application;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;
import org.joml.*;

public abstract class Application {
    private boolean exit=false;
    private long lastFrame,frameTime;
    private int targetFPS;
    public Application(int openglVersionMajor,int openglVersionMinor,int targetFPS){
        this.targetFPS=targetFPS;
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,openglVersionMajor);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,openglVersionMinor);
        glfwWindowHint(GLFW_OPENGL_PROFILE,GLFW_OPENGL_CORE_PROFILE);
        setup();
    }

    public void run(){
        lastFrame=System.nanoTime()-1000_000_000/targetFPS;
        while (!exit){
            long startTime=System.nanoTime();
            loop((float)((double)(startTime-lastFrame)/1000_000_000d));
            lastFrame=startTime;
            long endTime=System.nanoTime();
            frameTime=endTime-startTime;
            sleep(1000_000_000/targetFPS-(endTime-startTime));
        }
    }

    public long getFrameTime() {
        return frameTime;
    }
    public int getFPS(){
        return (int)(1000_000_000/frameTime);
    }

    public void sleep(long time){
        try{
            time=time>0?time:0;
            Thread.sleep(time/1000_000,(int)(time%1000_000));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        exit=true;
    }

    public abstract void setup();
    public abstract void loop(float delta);
}
