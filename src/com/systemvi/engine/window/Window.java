package com.systemvi.engine.window;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Window {
    private KeyListener keyPress=null,keyRelease=null;
    private long id;
    public Window(int width,int height,String title){
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
    public void addOnClickListener(MouseClickListener listener){
        glfwSetMouseButtonCallback(id,(long window, int button, int action, int mods)-> {
            listener.click(button,action,mods);
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
            if((action&GLFW_PRESS)!=0&&keyPress!=null)keyPress.key(key,scancode,action,mods);
            if((action&GLFW_RELEASE)!=0&&keyRelease!=null)keyRelease.key(key,scancode,action,mods);
        });
    }
    public void addOnKeyReleaseListener(KeyListener listener){
        keyRelease=listener;
        glfwSetKeyCallback(id,(long window,int key,int scancode,int action,int mods)->{
            if((action&GLFW_PRESS)!=0&&keyPress!=null)keyPress.key(key,scancode,action,mods);
            if((action&GLFW_RELEASE)!=0&&keyRelease!=null)keyRelease.key(key,scancode,action,mods);
        });
    }
    public void addOnResizeListener(ResizeListener listener){
        glfwSetFramebufferSizeCallback(id,(long windw,int width,int height)->{
            glViewport(0,0,width,height);
            listener.resize(width,height);
        });
    }
    public boolean shouldClose(){
        return glfwWindowShouldClose(id);
    }
    public static void pollEvents(){
        glfwPollEvents();
    }
    public void swapBuffers(){
        glfwSwapBuffers(id);
    }
}
