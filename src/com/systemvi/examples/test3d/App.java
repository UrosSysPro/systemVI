package com.systemvi.examples.test3d;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.window.Window;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp.Functions.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;

public class App extends Application {

    public App(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }
    public Mesh mesh;
    public Shader shader;
    public Camera camera;
    public Window window;
    float x=0,y=0,z=50;

    @Override
    public void setup() {
        window=new Window(800,600,"Test 3d");
        mesh=new Mesh(
            new VertexAttribute("position",3),
            new VertexAttribute("normal", 3),
            new VertexAttribute("uv",2)
        );

        mesh.setVertexData(new float[]{
            //position      normal      uv
            -100,  100, 0,      0,0,1,      0,1,
             100,  100, 0,      0,0,1,      1,1,
            -100, -100, 0,      0,0,1,      0,0,
             100, -100, 0,      0,0,1,      0,1,
        });
        mesh.setIndices(new int[]{
            0,1,2,
            1,2,3
        });
        camera=new Camera();
        camera.setPosition(0,0,50);
        camera.setScreenSize(800,600);
        camera.setPerspectiveProjection((float)Math.toRadians(60),0.1f,100);
        camera.update();
        shader=new Shader(
            "assets/examples/test3d/vertex.glsl",
            "assets/examples/test3d/fragment.glsl"
        );
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
        window.addOnKeyPressListener((key, scancode, mods) -> {
            if(key==GLFW_KEY_W)z-=0.5;
            if(key==GLFW_KEY_S)z+=0.5;
        });
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();

        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);


        camera.setPosition(x,y,z);
        camera.update();

        shader.use();
        shader.setUniform("view",camera.getView());
        shader.setUniform("projection",camera.getProjection());
        mesh.drawElements(2);

        window.swapBuffers();
    }
}
