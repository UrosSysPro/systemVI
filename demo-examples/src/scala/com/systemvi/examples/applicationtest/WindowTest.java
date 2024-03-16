package com.systemvi.examples.applicationtest;

import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.window.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class WindowTest {
    Window window;
    Mesh mesh;
    Shader shader;
    public WindowTest(){
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,3);
        glfwWindowHint(GLFW_OPENGL_PROFILE,GLFW_OPENGL_CORE_PROFILE);
        window=new Window(800,600,"Window Test");
        mesh=new Mesh(new VertexAttribute("position",3),new VertexAttribute("color",4));
        mesh.setVertexData(new float[]{
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            0.0f,  0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            -0.25f, -0.25f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
             0.25f, -0.25f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
             0.0f,   0.25f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
        });
        shader= Shader.builder()
            .fragment("assets/applicationtest/fragment.glsl")
            .vertex("assets/applicationtest/vertex.glsl")
            .build();
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
    }

    public void run(){
        while(!window.shouldClose()){

            window.pollEvents();
            glClearColor(0.5f,0.3f,0.8f,1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            shader.use();
            mesh.draw();

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
