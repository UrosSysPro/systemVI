package com.systemvi.examples.applicationtest;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.window.Window;

import static org.lwjgl.opengl.GL33.*;

public class ApplicationTest extends Application {
    Window window;
    Mesh mesh;
    Shader shader;

    public ApplicationTest(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    @Override
    public void setup() {
        window=new Window(800,600,"App");
        mesh=new Mesh(
            new VertexAttribute("position",3),
            new VertexAttribute("color",4)
        );
        mesh.setVertexData(new float[]{
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            0.0f,  0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            -0.25f, -0.25f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.25f, -0.25f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            0.0f,   0.25f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
        });
        shader= Shader.builder()
            .fragment("applicationtest/fragment.glsl")
            .vertex("applicationtest/vertex.glsl")
            .build();
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();
        glClearColor(0.4f,0.2f,0.9f,1.0f);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        shader.use();
        mesh.draw();
        window.swapBuffers();
    }
}
