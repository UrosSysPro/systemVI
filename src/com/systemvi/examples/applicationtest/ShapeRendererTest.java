package com.systemvi.examples.applicationtest;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.noise.Perlin2d;
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

    Perlin2d noise;
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
        noise=new Perlin2d((int)System.currentTimeMillis(),80,60);
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        OpenGLUtils.clear(0,0,0,1,OpenGLUtils.Buffer.COLOR_BUFFER);
        window.pollEvents();
        renderer.setCamera(camera);

        int size=10;
        Vector4f color=new Vector4f();
        for(int i=0;i<80;i++){
            for(int j=0;j<60;j++){
                float value=noise.get(
                    (float)i/10,
                    (float)j/10
                );
                color.set(value);
                renderer.rect(i*size,j*size,size,size,color);
            }
        }

        renderer.flush();
        window.swapBuffers();
    }
}
