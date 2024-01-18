package com.systemvi.engine.window;

import java.util.ArrayList;
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

        openglInfo=glGetString(GL_VERSION);
        System.out.println(openglInfo);

        registerListeners();
    }

    public void addOnMouseDownListener(MousePressListener listener){
        mouseDownEvents.add(listener);
    }
    public void addOnMouseUpListener(MousePressListener listener){
        mouseUpEvents.add(listener);
    }
    public void addOnMouseMoveListener(MouseMoveListener listener){
        mouseMoveEvents.add(listener);
    }
    public void addOnKeyPressListener(KeyListener listener){
        keyDownEvents.add(listener);
    }
    public void addOnKeyReleaseListener(KeyListener listener){
        keyUpEvents.add(listener);
    }
    public void addOnResizeListener(ResizeListener listener){
        resizeEvents.add(listener);
    }
    public void addOnScrollListener(ScrollListener listener){
        scrollEvents.add(listener);
    }

    public void removeOnMouseDownListener(MousePressListener listener){
        mouseDownEvents.remove(listener);
    }
    public void removeOnMouseUpListener(MousePressListener listener){
        mouseUpEvents.remove(listener);
    }
    public void removeOnMouseMoveListener(MouseMoveListener listener){
        mouseMoveEvents.remove(listener);
    }

    public void removeOnKeyDownListener(KeyListener listener){
        keyDownEvents.remove(listener);
    }
    public void removeOnKeyUpListener(KeyListener listener){
        keyUpEvents.remove(listener);
    }

    public void removeOnResizeListener(ResizeListener listener){
        resizeEvents.remove(listener);
    }
    public void removeOnScrollListener(ScrollListener listener){
        scrollEvents.remove(listener);
    }

    private void registerListeners(){
        mousePosition=new Vector2d(0,0);

        //keyboard events
        keyUpEvents=new ArrayList<>();
        keyDownEvents=new ArrayList<>();
        glfwSetKeyCallback(id,(long window,int key,int scancode,int action,int mods)->{
            if(action==GLFW_PRESS){
                for(KeyListener listener:keyDownEvents){
                    listener.key(key,scancode,mods);
                }
            }
            if(action==GLFW_RELEASE){
                for(KeyListener listener:keyUpEvents){
                    listener.key(key,scancode,mods);
                }
            }
            if(action==GLFW_REPEAT){

            }
        });
        //mouse events
        mouseDownEvents=new ArrayList<>();
        mouseUpEvents=new ArrayList<>();
        mouseMoveEvents=new ArrayList<>();
        glfwSetMouseButtonCallback(id,(window,button, action, mods) -> {
            if(action==GLFW_PRESS){
                for(MousePressListener listener:mouseDownEvents){
                    listener.mousePress(button,mods);
                }
            }
            if(action==GLFW_RELEASE){
                for(MousePressListener listener:mouseUpEvents){
                    listener.mousePress(button,mods);
                }
            }
            if(action==GLFW_REPEAT){

            }
        });
        glfwSetCursorPosCallback(id,(long window,double x,double y)->{
            mousePosition.x=x;
            mousePosition.y=y;
            for(MouseMoveListener listener:mouseMoveEvents){
                listener.move(x,y);
            }
        });
        //resize events
        resizeEvents=new ArrayList<>();
        glfwSetFramebufferSizeCallback(id,(long windw,int w,int h)->{
            this.width=w;
            this.height=h;
            glViewport(0,0,w,h);
            for(ResizeListener listener:resizeEvents){
                listener.resize(w,h);
            }
        });
        //scroll events
        scrollEvents=new ArrayList<>();
        glfwSetScrollCallback(id, (long window, double xoffset, double yoffset)->{
            for(ScrollListener listener:scrollEvents){
                listener.scroll(xoffset,yoffset);
            }
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
