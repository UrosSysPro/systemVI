package com.systemvi.engine.application;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;
import org.joml.*;

public abstract class Application {
    private final long nanosInSecond,millisInSecond,microsInSecond;
    private final int maxNanos;
    private boolean exit=false;
    private double lastFrame,frameTime;
    private int targetFPS;
    private double targetFrameTime;
    public Application(int openglVersionMajor,int openglVersionMinor,int targetFPS){
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
    }

    public void run(){
        setup();
        lastFrame=glfwGetTime()-targetFrameTime;
        while(!exit){
            double startTime=glfwGetTime();
            loop((float)(startTime-lastFrame));
            double endTime=glfwGetTime();
            frameTime=endTime-startTime;

            sleep(targetFrameTime-frameTime);

            lastFrame=startTime;
        }
        dispose();
    }

    public double getFrameTime() {
        return frameTime;
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

    public abstract void setup();
    public abstract void loop(float delta);
}
