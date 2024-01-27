package com.systemvi.examples.fractals;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.OpenGLUtils;
import com.systemvi.engine.window.Window;

public class Mandelbrotset extends Game {

    public Mandelbrotset(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    TextureRenderer renderer;
    Texture texture;
    Camera camera;
    Shader shader;
    @Override
    public void setup(Window window) {
        camera=Camera.default2d(window);
        renderer=new TextureRenderer();
        renderer.setCamera(camera);
        texture=new Texture(1,1, Format.RGB);
        shader= Shader.builder()
                .fragment("assets/examples/fractals/mandelbrot.glsl")
                .vertex("assets/renderer/textureRenderer/vertex.glsl")
                .build();
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
        renderer.setShader(shader);
    }

    @Override
    public void loop(float delta) {
        OpenGLUtils.clear(0,0,0,0, OpenGLUtils.Buffer.COLOR_BUFFER);
        renderer.draw(texture,0,0,800,600);
        renderer.flush();
    }
}
