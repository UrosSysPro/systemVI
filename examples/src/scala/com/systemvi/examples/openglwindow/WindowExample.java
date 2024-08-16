package com.systemvi.examples.openglwindow;

import org.lwjgl.opengl.GL;

import java.io.File;
import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class WindowExample {

    public long window;
    public int vertexBuffer,shaderProgram,attributeBuffer;

    public float r=0,g=0,b=0.5f;

    public WindowExample(){
        init();
        createWindow();
        createMesh();
        createShader();
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
    public void createMesh(){
        float[] vertices = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f,  0.5f, 0.0f
        };
        attributeBuffer=glGenVertexArrays();
        vertexBuffer=glGenBuffers();
        glBindVertexArray(attributeBuffer);
        glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3*4 , 0);
        glEnableVertexAttribArray(0);
        //pppccc
        //pcpcpc
    }
    public void createShader(){
        try{
            File file;
            Scanner scanner;
            file=new File("assets/openglwindow/vertex.glsl");
            scanner=new Scanner(file);
            String vertexSource="";
            while(scanner.hasNext()){
                vertexSource+=scanner.nextLine()+"\n";
            }
            scanner.close();
            int vertexShader=glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertexShader,vertexSource);
            glCompileShader(vertexShader);
            int[] status=new int[1];
            glGetShaderiv(vertexShader,GL_COMPILE_STATUS,status);
            System.out.println("status: "+status[0]);
            if(status[0]==0){
                String log=glGetShaderInfoLog(vertexShader);
                System.out.println(log);
            }

            file=new File("assets/openglwindow/fragment.glsl");
            scanner=new Scanner(file);
            String fragmentSource="";
            while (scanner.hasNext()){
                fragmentSource+=scanner.nextLine()+"\n";
            }
            scanner.close();
            int fragmentShader=glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragmentShader,fragmentSource);
            glCompileShader(fragmentShader);
            glGetShaderiv(fragmentShader,GL_COMPILE_STATUS,status);
            System.out.println("status: "+status[0]);
            if(status[0]==0){
                String log=glGetShaderInfoLog(fragmentShader);
                System.out.println(log);
            }

            shaderProgram=glCreateProgram();
            glAttachShader(shaderProgram,vertexShader);
            glAttachShader(shaderProgram,fragmentShader);
            glLinkProgram(shaderProgram);

            glGetProgramiv(shaderProgram,GL_LINK_STATUS,status);
            if(status[0]==0){
                String log=glGetProgramInfoLog(shaderProgram);
                System.out.println(log);
            }
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            glUseProgram(shaderProgram);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // java -> vertex -> raster -> fragment
    public void loop(){
        while(!glfwWindowShouldClose(window)){
            glfwPollEvents();
            glClearColor(r,g,b,1.0f);
            glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
            //input
            //update
            //draw
            glUseProgram(shaderProgram);
            glBindVertexArray(attributeBuffer);
            glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
            glLineWidth(3.0f);
            glDrawArrays(GL_TRIANGLES,0,3);
            glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);

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
