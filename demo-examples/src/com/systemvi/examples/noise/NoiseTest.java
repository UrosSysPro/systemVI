package com.systemvi.examples.noise;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureData;
import com.systemvi.engine.window.Window;
import org.joml.Vector4f;

public class NoiseTest extends Game {
    public NoiseTest(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    private Camera camera;
    private Texture texture;
    private TextureRenderer renderer;

    @Override
    public void setup(Window window) {
        camera = new Camera();
        camera.setPosition(400, 300, 0);
        camera.setScreenSize(800, 600);
        camera.setScale(1, -1, 1);
        camera.update();
        texture = new Texture(window.getWidth(), window.getHeight(), Format.RGBA32);
        renderer = new TextureRenderer();
        renderer.setCamera(camera);
        TextureData data = new TextureData(window.getWidth(), window.getHeight(), Format.RGBA);
        for (int i = 0; i < window.getWidth(); i++) {
            for (int j = 0; j < window.getHeight(); j++) {
                data.setPixel4f(i, j, new Vector4f(SimpleNoise.getStripes(((float)i / 30), (float) Math.PI / 60), 0.6f, 0.0f, 1.0f));
            }
        }
        texture.setData(data);
    }

    @Override
    public void loop(float delta) {
        renderer.draw(texture, 0, 0, texture.getWidth(), texture.getHeight());
        renderer.flush();
    }
}
