package com.systemvi.examples.compute;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.shader.ComputeShader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.window.Window;

import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;

public class Gradient extends Game {
    public Gradient(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }
    ComputeShader compute;
    Texture texture;
    @Override
    public void setup(Window window) {
        compute=new ComputeShader("assets/examples/compute/gradient/compute.glsl");
        texture=new Texture(800,600, Format.RGBA32);
    }

    @Override
    public void loop(float delta) {
        texture.bind(0);
        glBindImageTexture(
                0,
                texture.getId(),
                0,
                false,
                0,
                GL_READ_WRITE,
                GL_RGBA32F
        );
        compute.dispatch(800,600,1);
    }
}
