package com.systemvi.examples.texturetest;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.FrameBuffer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.OpenGLUtils;
import com.systemvi.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Paint extends Game {
    public Paint(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    public Camera camera;
    public ShapeRenderer renderer;

    public Texture texture;
    public FrameBuffer frameBuffer;
    public TextureRenderer textureRenderer;
    public Vector2f current, previous;

    public boolean mouseDown;
    @Override
    public void setup(Window window) {
        current=new Vector2f();
        previous=new Vector2f();
        mouseDown=false;
        camera=Camera.default2d(window);
        renderer=new ShapeRenderer();
        renderer.setCamera(camera);
        textureRenderer=new TextureRenderer();
        textureRenderer.setCamera(camera);
        texture=new Texture(800,600, Format.RGB);
        frameBuffer=FrameBuffer.builder()
            .color(texture)
            .build();
    }
    @Override
    public void loop(float delta) {
        OpenGLUtils.clear(0,0,0,0, OpenGLUtils.Buffer.COLOR_BUFFER);
        frameBuffer.begin();
        camera.setScale(1,1,1);
        camera.update();
        if(mouseDown){
            renderer.line(current,previous,3,new Vector4f(0.3f,0.8f,0.5f,1.0f));
            renderer.flush();
        }
        previous.set(current);
        frameBuffer.end();
        camera.setScale(1,-1,1);
        camera.update();
        textureRenderer.draw(texture,0,0,800,600);
        textureRenderer.flush();
    }
    @Override
    public boolean mouseDown(int button, int mods, double x, double y) {
        mouseDown=true;
        return true;
    }
    @Override
    public boolean mouseUp(int button, int mods, double x, double y) {
        mouseDown=false;
        return true;
    }
    @Override
    public boolean mouseMove(double x, double y) {
        current.set((float)x,(float)y);
        return true;
    }
}
