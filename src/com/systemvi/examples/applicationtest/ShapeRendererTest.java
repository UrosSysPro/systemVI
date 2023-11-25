package com.systemvi.examples.applicationtest;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.utils.OpenGLUtils;
import com.systemvi.engine.window.Window;
import static org.lwjgl.opengl.GL33.*;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class ShapeRendererTest extends Application {

    public ShapeRendererTest(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }
    Window window;
    ShapeRenderer renderer;
    Camera camera;
    float angle=0;
    @Override
    public void setup() {
        window=new Window(800,600,"Shape Renderer");
        renderer=new ShapeRenderer();
        Shader shader=new Shader(
            "assets/renderer/shapeRenderer/vertex.glsl",
            "assets/examples/shapeRendererTest/fragment.glsl"
        );
//        renderer.setShader(shader);
        camera=new Camera();
        camera.setScreenSize(800,600);
        camera.setPosition(400,300,0);
        camera.setScale(1,-1,1);
        camera.update();
        window.addOnResizeListener((width,height)->{
            camera.setPosition(width/2,height/2,0);
            camera.setScreenSize(width,height);
            camera.update();
        });
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        OpenGLUtils.clear(0,0,0,1,OpenGLUtils.Buffer.COLOR_BUFFER);
        window.pollEvents();
        renderer.setCamera(camera);

        renderer.rect(100,100,50,50,new Vector4f(1),angle);

        renderer.regularPolygon(5,30,new Vector2f(200,200),new Vector4f(0.4f,0.2f,0.8f,1.0f),angle);

        renderer.rect(280,280,40,40,new Vector4f(0.7f,0.5f,0.4f,1.0f));

        Vector2f start=new Vector2f((float)Math.cos(angle)*20+300,(float)Math.sin(angle)*20+300);
        Vector2f end=new Vector2f((float)Math.cos(angle+Math.PI)*20+300,(float)Math.sin(angle+Math.PI)*20+300);
        renderer.line(start,end,3,new Vector4f(0.4f,0.7f,0.1f,1.0f));

        renderer.line(new Vector2f(0,0),new Vector2f(800,600),5,new Vector4f(0.5f,0.2f,0.6f,1.0f));
        renderer.line(new Vector2f(800,0),new Vector2f(0,600),5,new Vector4f(0.5f,0.2f,0.6f,1.0f));

        renderer.flush();
        angle+=delta;
        window.swapBuffers();
    }
}
