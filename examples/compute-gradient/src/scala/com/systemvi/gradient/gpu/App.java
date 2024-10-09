package com.systemvi.gradient.gpu;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class App extends Game {
    public App() {
        super(3,3,60,800,600,"Gradient");
    }
    Shader compute;
    Texture texture;
    TextureRenderer renderer;
    Camera camera;
    @Override
    public void setup(Window window) {
        compute= Shader.builder()
            .compute("compute.glsl")
            .build();
        texture=new Texture(800,600, Format.RGBA32F);
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