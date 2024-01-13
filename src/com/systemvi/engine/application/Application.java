package com.systemvi.engine.application;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;
import org.joml.*;

public abstract class Application {
    private long nanosInSecond;
    private boolean exit=false;
    private double lastFrame,frameTime;
    private int targetFPS;
    private long targetFrameTime;
    public Application(int openglVersionMajor,int openglVersionMinor,int targetFPS){
        nanosInSecond=1000_000_000L;
        this.targetFPS=targetFPS;
        targetFrameTime=nanosInSecond/targetFPS;
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,openglVersionMajor);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,openglVersionMinor);
        glfwWindowHint(GLFW_OPENGL_PROFILE,GLFW_OPENGL_CORE_PROFILE);
        setup();
    }

    public void run(){
        lastFrame=glfwGetTime()-targetFrameTime;
        while(!exit){
            double startTime=glfwGetTime();
            loop((float)(startTime-lastFrame));
            double endTime=glfwGetTime();
            frameTime=endTime-startTime;

            sleep(targetFrameTime-(long)(frameTime*nanosInSecond));

            lastFrame=startTime;
        }
    }

    public double getFrameTime() {
        return frameTime;
    }
    public int getFPS(){
        if(frameTime==0)return (int)nanosInSecond;
        return (int)(nanosInSecond/frameTime);
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
