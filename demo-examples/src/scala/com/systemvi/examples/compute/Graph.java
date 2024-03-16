package com.systemvi.examples.compute;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.shader.ShaderStorage;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;

public class Graph extends Game {
    public Graph(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    Shader compute;
    ShaderStorage storage;
    Shader shader;
    Texture texture;
    TextureRenderer renderer;
    Camera camera;

    float time=0;

    @Override
    public void setup(Window window) {
        camera=Camera.default2d(window);
        texture=new Texture(window.getWidth(),window.getHeight(), Format.RGB);
        renderer=new TextureRenderer();
        renderer.setCamera(camera);
        compute=Shader.builder().compute("assets/examples/compute/graph/compute.glsl").build();
        shader= Shader.builder()
            .vertex("assets/renderer/textureRenderer/vertex.glsl")
            .fragment("assets/examples/compute/graph/fragment.glsl")
            .build();
        renderer.setShader(shader);
        storage=new ShaderStorage(new float[800]);

        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
    }

    @Override
    public void loop(float delta) {
        time+=delta;
        //write to shader storage
        compute.use();
        compute.setUniform("time",time);
        storage.bind(0);
        compute.dispatch(800,1,1);
        Utils.barrier(Utils.Barrier.SHADER_STORAGE);
        //draw graph

        renderer.draw(texture,0,0,800,600);
        renderer.flush();
    }
}
