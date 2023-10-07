package com.systemvi.examples.applicationtest;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.window.Window;
import static org.lwjgl.opengl.GL33.*;
import org.joml.Vector4f;

public class ShapeRendererTest extends Application {

    public ShapeRendererTest(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }
    Window window;
    ShapeRenderer renderer;
    Camera camera;
    @Override
    public void setup() {
        window=new Window(800,600,"Shape Renderer");
        renderer=new ShapeRenderer();
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
        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);
        window.pollEvents();
        renderer.setCamera(camera);
//        renderer.rect(100,100,300,300,new Vector4f(0.3f,0.5f,0.7f,1.0f));
        for(int i=0;i<100;i++){
            for(int j=0;j<100;j++){
                renderer.rect(i*20,j*20,10,10,new Vector4f(0.3f,0.5f,0.7f,1.0f));
            }
        }
        renderer.flush();
        window.swapBuffers();
    }
}
