package com.systemvi.examples.noise;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.noise.*;
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
        texture = new Texture(window.getWidth(), window.getHeight(), Format.RGBA32F);
        renderer = new TextureRenderer();
        renderer.setCamera(camera);
        TextureData data = new TextureData(window.getWidth(), window.getHeight(), Format.RGBA);
        PerlinNoise perlin = new PerlinNoise(window.getWidth(), window.getHeight());
        StripesNoise stripes = new StripesNoise(60, 30);
        CheckerboardNoise checker = new CheckerboardNoise(30);
        RippleNoise ripple = new RippleNoise(400, 300, 10);
        WhiteNoise white = new WhiteNoise(450, 800, 600);
        ValueNoise val = new ValueNoise(6352, 900);
        WorleyNoise worley = new WorleyNoise(800, 600, 50, 54547);
        for (int i = 0; i < window.getWidth(); i++) {
            for (int j = 0; j < window.getHeight(); j++) {
//                float value = stripes.get(i, j);
//                float value = checker.get(i, j);
//                float value = perlin.get(i, j);
//                float value = ripple.get(i, j);
//                float value = white.get(i, j);
//                float value = val.get(i, j);
//                float value = worley.get(i, j);
                float value = NoiseFunction.Islands(i, j, val);
                data.set(i, j, new Vector4f(value, value, value, 1.0f));
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
