package com.systemvi.test;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.noise.Perlin2d;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
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
    float scale=10;
    int octaves=4;
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
        noise=new Perlin2d((int)System.currentTimeMillis(),2000,2000);
        window.addOnMouseMoveListener((x,y) -> {
            scale=(float)(5+x/800*40);
            octaves=(int)(y/600*4);
        });
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        Utils.clear(0,0,0,1, Utils.Buffer.COLOR_BUFFER);
        window.pollEvents();
        renderer.setCamera(camera);

        int size=10;
        Vector4f color=new Vector4f();
        for(int i=0;i<80;i++){
            for(int j=0;j<60;j++){
                float value=0;
                int stepen=1;
                for(int k=0;k<octaves;k++){
                    value+=noise.get(
                        i*scale/stepen,
                        j*scale/stepen
                    )/stepen;
                    stepen*=2;
                }
                color.set(value);
                renderer.rect(i*size,j*size,size,size,color);
            }
        }

        renderer.flush();
        window.swapBuffers();
    }
}
