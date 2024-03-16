package com.systemvi.examples.applicationtest;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.window.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class CameraTest extends Application {

    Window window;
    Mesh mesh;
    Shader shader;
    Camera camera;
    float rotation=0,x=400,y=300,zoom=1;

    public CameraTest(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    @Override
    public void setup() {
        window=new Window(800,600,"Camera Test");
        shader= Shader.builder()
            .fragment("assets/cameraTest/fragment.glsl")
            .vertex("assets/cameraTest/vertex.glsl")
            .build();
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
        mesh=new Mesh(
            new VertexAttribute("position",3),
            new VertexAttribute("color",4)
        );
        mesh.setVertexData(new float[]{
            //position //color
            100,100,0,   1,0,0,1,
            250,100,0,   0,0,1,1,
            100,250,0,   1,0,0,1,

            250,100,0,   0,0,1,1,
            100,250,0,   1,0,0,1,
            250,250,0,   0,0,1,1
        });
        camera=new Camera();
        camera.setPosition(400,300,1);
        camera.setScale(1,-1,1);
        camera.setScreenSize(800,600);
//        camera.setOrthographicProjection(0,1000);
//        camera.setPerspectiveProjection((float) Math.PI/3f,0.1f,1000f);
        camera.update();

        window.addOnResizeListener((int width,int height)->{
            camera.setScreenSize(width,height);
            camera.setPosition(width/2,height/2,0);
            camera.update();
        });
        window.addOnKeyPressListener((int key, int scancode, int mods)-> {
            if(key==GLFW_KEY_UP)rotation+=0.05;
            if(key==GLFW_KEY_DOWN)rotation-=0.05;

            float speed=5*zoom;
            float rightX=(float) Math.cos(rotation)*speed;
            float rightY=(float) Math.sin(rotation)*speed;
            float upX=(float) Math.cos(rotation+(float)Math.PI/2f)*speed;
            float upY=(float) Math.sin(rotation+(float)Math.PI/2f)*speed;

            if(key==GLFW_KEY_W){x-=upX;y-=upY;}
            if(key==GLFW_KEY_S){x+=upX;y+=upY;}
            if(key==GLFW_KEY_A){x-=rightX;y-=rightY;}
            if(key==GLFW_KEY_D){x+=rightX;y+=rightY;}

            if(key==GLFW_KEY_Q)zoom*=0.9f;
            if(key==GLFW_KEY_E)zoom*=1.1f;

            camera.setScale(zoom,-zoom,1);
            camera.setPosition(x,y,1);
            camera.setRotation(0,0,rotation);
            camera.update();
        });
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();
        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);
        shader.use();
        shader.setUniform("view",camera.getView());
        shader.setUniform("projection",camera.getProjection());
        mesh.draw();
        window.swapBuffers();
    }
}
