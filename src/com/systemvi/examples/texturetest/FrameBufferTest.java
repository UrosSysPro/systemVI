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
import org.joml.Vector4f;

public class FrameBufferTest extends Game {
    public FrameBufferTest(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    public Texture texture;
    public TextureRenderer renderer;
    public ShapeRenderer shapeRenderer;
    public Camera camera;
    public FrameBuffer frameBuffer;
    @Override
    public void setup(Window window) {
        texture=new Texture(800,600, Format.RGBA);
        camera=Camera.default2d(window);
        frameBuffer=new FrameBuffer(new Texture[]{texture},null,null);

        renderer=new TextureRenderer();
        renderer.setCamera(camera);
        shapeRenderer=new ShapeRenderer();
        shapeRenderer.setCamera(camera);

        frameBuffer.begin();
        System.out.println(frameBuffer.isCoplete());
        frameBuffer.end();
    }

    @Override
    public void loop(float delta) {
        frameBuffer.begin();
        OpenGLUtils.clear(0.6f,0.3f,0.9f,1.0f, OpenGLUtils.Buffer.COLOR_BUFFER);
        shapeRenderer.rect(100,100,100,100,new Vector4f(0.2f,0.7f,0.5f,1.0f));
        shapeRenderer.flush();
        frameBuffer.end();

        OpenGLUtils.clear(0.3f,0.6f,0.9f,1.0f, OpenGLUtils.Buffer.COLOR_BUFFER);

//        texture.bind(0);
        renderer.draw(texture,0,0, texture.getWidth(), texture.getHeight());
        renderer.flush();
    }

    @Override
    public void dispose() {

    }
}
