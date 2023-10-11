package com.systemvi.engine.window;

import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Window {
    private KeyListener keyPress=null,keyRelease=null;
    private MousePressListener mouseUp=null,mouseDown=null;
    private int width,height;
    private long id;
    public Window(int width,int height,String title){
        this.width=width;
        this.height=height;
        id=glfwCreateWindow(width,height,title,0,0);
        if(id==0){
            System.out.println("[ERROR] Cant create window");
        }
        glfwMakeContextCurrent(id);
        GL.createCapabilities();
        glViewport(0,0,width,height);
        addOnResizeListener((int w,int h)->{});
    }
    public void use(){
        glfwMakeContextCurrent(id);
    }

    public long getId() {
        return id;
    }

    public void addOnMouseDownListener(MousePressListener listener){
        mouseDown=listener;
        glfwSetMouseButtonCallback(id,(window,button,action,mods)->{
           if(action==GLFW_PRESS&&mouseDown!=null)mouseDown.mousePress(button,mods);
           if(action==GLFW_RELEASE&&mouseUp!=null)mouseUp.mousePress(button,mods);
        });
    }
    public void addOnMouseUpListener(MousePressListener listener){
        mouseUp=listener;
        glfwSetMouseButtonCallback(id,(window,button,action,mods)->{
            if(action==GLFW_PRESS&&mouseDown!=null)mouseDown.mousePress(button,mods);
            if(action==GLFW_RELEASE&&mouseUp!=null)mouseUp.mousePress(button,mods);
        });
    }
    public void addOnMouseMoveListener(MouseMoveListener listener){
        glfwSetCursorPosCallback(id,(long window,double x,double y)->{
           listener.move(x,y);
        });
    }
    public void addOnKeyPressListener(KeyListener listener){
        keyPress=listener;
        glfwSetKeyCallback(id,(long window,int key,int scancode,int action,int mods)->{
            if((action==GLFW_PRESS)&&keyPress!=null)keyPress.key(key,scancode,mods);
            if((action==GLFW_RELEASE)&&keyRelease!=null)keyRelease.key(key,scancode,mods);
        });
    }
    public void addOnKeyReleaseListener(KeyListener listener){
        keyRelease=listener;
        glfwSetKeyCallback(id,(long window,int key,int scancode,int action,int mods)->{
            if((action==GLFW_PRESS)&&keyPress!=null)keyPress.key(key,scancode,mods);
            if((action==GLFW_RELEASE)&&keyRelease!=null)keyRelease.key(key,scancode,mods);
        });
    }
    public void addOnResizeListener(ResizeListener listener){
        glfwSetFramebufferSizeCallback(id,(long windw,int width,int height)->{
            this.width=width;
            this.height=height;
            glViewport(0,0,width,height);
            listener.resize(width,height);
        });
    }
    public void addOnScrollListener(ScrollListener listener){
        glfwSetScrollCallback(id, (long window, double xoffset, double yoffset)->{
            listener.scroll(xoffset,yoffset);
        });
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean shouldClose(){
        return glfwWindowShouldClose(id);
    }
    public void pollEvents(){
        glfwPollEvents();
    }
    public void swapBuffers(){
        glfwSwapBuffers(id);
    }
    public void close(){
        glfwDestroyWindow(id);
    }
}
