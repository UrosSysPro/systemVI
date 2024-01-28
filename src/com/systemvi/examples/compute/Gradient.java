package com.systemvi.examples.compute;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.shader.ComputeShader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;

public class Gradient extends Game {
    public Gradient(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }
    ComputeShader compute;
    Texture texture;
    TextureRenderer renderer;
    Camera camera;
    @Override
    public void setup(Window window) {
        compute=new ComputeShader("assets/examples/compute/gradient/compute.glsl");
        texture=new Texture(800,600, Format.RGBA32);
        renderer=new TextureRenderer();
        camera=Camera.default2d(window);
        renderer.setCamera(camera);
    }

    @Override
    public void loop(float delta) {
        texture.bindAsImage(0);
        compute.use();
        compute.dispatch(800,600,1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);

        renderer.draw(texture,0,0,texture.getWidth(),texture.getHeight());
        renderer.flush();
    }
}
