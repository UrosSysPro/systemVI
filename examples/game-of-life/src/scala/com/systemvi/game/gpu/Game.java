package com.systemvi.game.gpu;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureData;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Random;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL43.*;


public class Game extends Application {
    public Game() {
        super(3,3,60);
    }

    private Window window;
    private Texture current, next;
    private TextureRenderer renderer;
    private Camera camera;
    private Shader shader;

    @Override
    public void setup() {
        window = new Window(800, 600, "Game of life");
        TextureData data = new TextureData(800, 600, Format.RGBA);
        Random r = new Random();
        for (int i = 0; i < data.getWidth(); i++) {
            for (int j = 0; j < data.getHeight(); j++) {
                data.set(i, j, new Vector4f(r.nextFloat(), 0, 0, 0));
            }
        }
        current = new Texture(800, 600, Format.RGBA32F);
        current.setData(data);
        next = new Texture(800, 600, Format.RGBA32F);
        camera = new Camera();
        camera.setPosition(400, 300, 0);
        camera.setScreenSize(800, 600);
        camera.setScale(1, -1, 1);
        camera.update();
        renderer = new TextureRenderer();
        renderer.setCamera(camera);
        shader= Shader.builder()
            .compute("compute.glsl")
            .build();
    }

    @Override
    public void loop(float delta) {
        if (window.shouldClose())close();
        window.pollEvents();

        glClearColor(1, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);

        current.bind();
        next.bind(1);
        glBindImageTexture(0, current.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        glBindImageTexture(1, next.getId(), 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        shader.use();
        shader.setUniform("size", new Vector2i(800, 600));
        shader.dispatch(current.getWidth(), current.getHeight(), 1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);
        Texture temp = current;
        current = next;
        next = temp;

        renderer.draw(current,0,0,current.getWidth(),current.getHeight());
        renderer.flush();

        window.swapBuffers();
    }
}
