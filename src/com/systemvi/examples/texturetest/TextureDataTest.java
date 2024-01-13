package com.systemvi.examples.texturetest;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureRegion;
import com.systemvi.engine.utils.OpenGLUtils;
import com.systemvi.engine.window.Window;

public class TextureDataTest extends Application {

    public TextureDataTest(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    public Window window;
    public Camera camera;
    public Texture texture;
    public TextureRenderer renderer;

    @Override
    public void setup() {
        window=new Window(800,600,"Texture Data Test");
        camera=new Camera();
        camera.setPosition(400,300,0);
        camera.setScreenSize(800,600);
        camera.setScale(1,-1,1);
        camera.update();
        renderer=new TextureRenderer();
        renderer.setCamera(camera);
        texture=new Texture("assets/examples/test3d/rock/diffuse.png");
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();

        OpenGLUtils.clear(0,0,0,0, OpenGLUtils.Buffer.COLOR_BUFFER, OpenGLUtils.Buffer.DEPTH_BUFFER);
        renderer.draw(texture,0,0,100,100);
        renderer.flush();

        window.swapBuffers();
    }
}
