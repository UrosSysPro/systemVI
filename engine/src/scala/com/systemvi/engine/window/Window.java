package com.systemvi.engine.window;

import java.util.ArrayList;

import com.systemvi.engine.utils.Utils;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Window {
    private KeyListener keyPress=null,keyRelease=null;
    private MousePressListener mouseUp=null,mouseDown=null;
    private int width,height;
    private final long id;
    private GLCapabilities capabilities;
    private ArrayList<KeyListener> keyUpEvents;
    private ArrayList<KeyListener> keyDownEvents;
    private ArrayList<MousePressListener> mouseUpEvents;
    private ArrayList<MousePressListener> mouseDownEvents;
    private ArrayList<MouseMoveListener> mouseMoveEvents;
    private ArrayList<ResizeListener> resizeEvents;
    private ArrayList<ScrollListener> scrollEvents;
    private Vector2d mousePosition;

    private InputProcessor inputProcessor;
    public Window(int width,int height,String title){
        this.width=width;
        this.height=height;
        id=glfwCreateWindow(width,height,title,0,0);
        if(id==0){
            System.out.println("[ERROR] Cant create window");
        }
        glfwMakeContextCurrent(id);
        capabilities=GL.createCapabilities();
        glViewport(0,0,width,height);

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

    public void setInputProcessor(InputProcessor processor){
        inputProcessor=processor;
    }

    private void registerListeners(){
        inputProcessor=null;
        mousePosition=new Vector2d(0,0);

        //keyboard events
        keyUpEvents=new ArrayList<>();
        keyDownEvents=new ArrayList<>();
        glfwSetKeyCallback(id,(long window,int key,int scancode,int action,int mods)->{
            if(action==GLFW_PRESS){
                if(inputProcessor!=null)inputProcessor.keyDown(key,scancode,mods);
                for(KeyListener listener:keyDownEvents){
                    listener.key(key,scancode,mods);
                }
            }
            if(action==GLFW_RELEASE){
                if(inputProcessor!=null)inputProcessor.keyUp(key,scancode,mods);
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
                if(inputProcessor!=null)inputProcessor.mouseDown(button,mods,mousePosition.x,mousePosition.y);
                for(MousePressListener listener:mouseDownEvents){
                    listener.mousePress(button,mods);
                }
            }
            if(action==GLFW_RELEASE){
                if(inputProcessor!=null)inputProcessor.mouseUp(button,mods,mousePosition.x,mousePosition.y);
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
            if(inputProcessor!=null)inputProcessor.mouseMove(mousePosition.x,mousePosition.y);
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
            if(inputProcessor!=null)inputProcessor.resize(w,h);
            for(ResizeListener listener:resizeEvents){
                listener.resize(w,h);
            }
        });
        //scroll events
        scrollEvents=new ArrayList<>();
        glfwSetScrollCallback(id, (long window, double xoffset, double yoffset)->{
            if(inputProcessor!=null)inputProcessor.scroll(xoffset,yoffset);
            for(ScrollListener listener:scrollEvents){
                listener.scroll(xoffset,yoffset);
            }
        });
    }

    public void use(){
        glfwMakeContextCurrent(id);
        GL.setCapabilities(capabilities);
        Utils.viewport(0,0,width,height);
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

    public String version(){
        return glGetString(GL_VERSION);
    }
    public String renderer(){
        return glGetString(GL_RENDERER);
    }
    public String extensions(){
        return glGetString(GL_EXTENSIONS);
    }
    public String vendor(){
        return glGetString(GL_VENDOR);
    }
    public String shadingLanguageVersion(){
        return glGetString(GL_SHADING_LANGUAGE_VERSION);
    }
    
    public void setTitle(String title){
        glfwSetWindowTitle(id, title);
    }
    public void setSize(int width, int height){
        glfwSetWindowSize(id, width, height);
    }
    public void setPosition(int x, int y){
        glfwSetWindowPos(id,x,y);
    }
    
    public static class Builder{
        private int width=-1,height=-1;
        private String title="";
        public Builder width(int width){
            this.width=width;
            return this;
        }
        public Builder height(int height){
            this.height=height;
            return this;
        }
        public Builder title(String title){
            this.title=title;
            return this;
        }
        public Builder size(int width,int height){
            width(width);
            height(height);
            return this;
        }
        public Window build(){
            return new Window(width,height,title);
        }
    }
    public static Builder builder(){
        return new Builder();
    }
}
