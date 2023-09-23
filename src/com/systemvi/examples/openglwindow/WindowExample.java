package com.systemvi.examples.openglwindow;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL33.*;

import static org.lwjgl.glfw.GLFW.*;

public class WindowExample {

    public long window;
    public float r=0,g=0,b=0.5f;

    public WindowExample(){
        init();
        createWindow();
        loop();
    }

    public void init(){
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,3);
        glfwWindowHint(GLFW_OPENGL_PROFILE,GLFW_OPENGL_CORE_PROFILE);
    }
    public void createWindow(){
        window=glfwCreateWindow(800,600,"Window",0,0);
        if(window==0){
            System.out.println("nece da se napravi prozor");
        }
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glViewport(0,0,800,600);

        glfwSetFramebufferSizeCallback(window,(long window, int width, int height)->{
            glViewport(0,0,width,height);
            System.out.println("resize"+width+" "+height);
        });
        glfwSetKeyCallback(window, (long window, int key, int scancode, int action, int mods)->{
            System.out.println(key);
        });
        glfwSetCursorPosCallback(window,(long win,double x,double y)->{
            r=(float)x/800;
            g=(float)y/600;
        });
    }
    public void loop(){
        while(!glfwWindowShouldClose(window)){
            glfwPollEvents();
            glClearColor(r,g,b,1.0f);
            glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
            //input
            //update
            //draw
            glfwSwapBuffers(window);
            sleep(16);
        }
    }
    public void sleep(long time){
        try{
            Thread.sleep(time);
        }catch (Exception e){

        }
    }
}
