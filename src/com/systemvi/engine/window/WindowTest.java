package com.systemvi.engine.window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class WindowTest {
    Window window;
    public WindowTest(){
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,3);
        glfwWindowHint(GLFW_OPENGL_PROFILE,GLFW_OPENGL_CORE_PROFILE);
        window=new Window(800,600,"Window Test");
    }
    public void run(){
        while(!window.shouldClose()){

            window.pollEvents();
            glClearColor(0.5f,0.3f,0.8f,1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            window.swapBuffers();
            sleep();
        }
    }
    public void sleep(){
        try{
            Thread.sleep(16);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
