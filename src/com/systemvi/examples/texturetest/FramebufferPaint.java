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

public class FramebufferPaint extends Game {
    public FramebufferPaint(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    public ShapeRenderer shapes;
    public TextureRenderer textureRenderer;
    public FrameBuffer frameBuffer;
    public Texture colorAttachment;
    public Vector2f mousePosition,previousMousePosition;
    public boolean mouseDown;

    @Override
    public void setup(Window window) {
        colorAttachment=new Texture(window.getWidth(),window.getHeight(), Format.RGB);
        frameBuffer=FrameBuffer.builder()
            .color(colorAttachment)
            .build();
        shapes=new ShapeRenderer();
        shapes.setCamera(Camera.default2d(window,window.getWidth()/2,window.getHeight()/2,false));

        textureRenderer=new TextureRenderer();
        textureRenderer.setCamera(Camera.default2d(window));

        mousePosition=new Vector2f(0,0);
        previousMousePosition=new Vector2f(0,0);

        frameBuffer.begin();
        OpenGLUtils.clear(0,0,0,0, OpenGLUtils.Buffer.COLOR_BUFFER);
        frameBuffer.end();
    }

    @Override
    public void loop(float delta) {
        frameBuffer.begin();
        if(mouseDown){
            shapes.line(previousMousePosition,mousePosition,3,new Vector4f(0.3f,0.6f,0.9f,1.0f));
            shapes.flush();
        }
        previousMousePosition.set(mousePosition);
        frameBuffer.end();

        OpenGLUtils.clear(0,0,0,0, OpenGLUtils.Buffer.COLOR_BUFFER);
        textureRenderer.draw(colorAttachment,0,0,colorAttachment.getWidth(),colorAttachment.getHeight());
        textureRenderer.flush();
    }

    @Override
    public boolean mouseDown(int button, int mods, double x, double y) {
        mouseDown=true;
        mousePosition.set((float)x,(float)y);
        previousMousePosition.set((float)x,(float)y);
        return true;
    }

    @Override
    public boolean mouseUp(int button, int mods, double x, double y) {
        mouseDown=false;
        return true;
    }

    @Override
    public boolean mouseMove(double x, double y) {
        mousePosition.set((float)x,(float)y);
        return true;
    }
}
