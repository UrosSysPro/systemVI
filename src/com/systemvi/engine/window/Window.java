package com.systemvi.engine.window;

import com.systemvi.examples.datastructures.ArrayList;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Window {
    private KeyListener keyPress=null,keyRelease=null;
    private MousePressListener mouseUp=null,mouseDown=null;
    private int width,height;
    private final long id;
    private ArrayList<KeyListener> keyUpEvents;
    private ArrayList<KeyListener> keyDownEvents;
    private ArrayList<MousePressListener> mouseUpEvents;
    private ArrayList<MousePressListener> mouseDownEvents;
    private ArrayList<MouseMoveListener> mouseMoveEvents;
    private ArrayList<ResizeListener> resizeEvents;
    private ArrayList<ScrollListener> scrollEvents;
    private Vector2d mousePosition;
    private final String openglInfo;
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
        openglInfo=glGetString(GL_VERSION);
        System.out.println(openglInfo);

        mousePosition=new Vector2d(0,0);

        //keyboard events
        keyUpEvents=new ArrayList<>();
        keyDownEvents=new ArrayList<>();
        glfwSetKeyCallback(id,(long window,int key,int scancode,int action,int mods)->{

        });
        //mouse events
        mouseDownEvents=new ArrayList<>();
        mouseUpEvents=new ArrayList<>();
        mouseMoveEvents=new ArrayList<>();
        glfwSetMouseButtonCallback(id,(window,button, action, mods) -> {

        });
        glfwSetCursorPosCallback(id,(long window,double x,double y)->{

        });
        //resize events
        resizeEvents=new ArrayList<>();
        glfwSetFramebufferSizeCallback(id,(long windw,int w,int h)->{

        });
        //scroll events
        scrollEvents=new ArrayList<>();
        glfwSetScrollCallback(id, (long window, double xoffset, double yoffset)->{

        });
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

    public String getOpenglInfo() {
        return openglInfo;
    }

    public void use(){
        glfwMakeContextCurrent(id);
    }
    public long getId() {
        return id;
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
