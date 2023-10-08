package com.systemvi.examples.applicationtest;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
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
//        for(int i=0;i<10;i++){
//            for(int j=0;j<10;j++){
//                renderer.rect(i*100,j*100,100,100,new Vector4f((float)i/10f,(float)j/10f,0.5f,1.0f));
//            }
//        }
        Vector2f[] points=new Vector2f[7];
        for(int i=0;i<points.length;i++){
            float ugao=(float)Math.PI*2/points.length*i;
            points[i]=new Vector2f();
            points[i].x=(float)Math.cos(ugao)*200+400;
            points[i].y=(float)Math.sin(ugao)*200+300;
        }
        renderer.polygon(points,new Vector4f(0.3f,0.2f,0.7f,1.0f));
        renderer.flush();
        window.swapBuffers();
    }
}
