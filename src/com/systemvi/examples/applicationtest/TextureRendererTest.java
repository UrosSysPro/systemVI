package com.systemvi.examples.applicationtest;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureRegion;
import com.systemvi.engine.window.Window;
import static org.lwjgl.opengl.GL33.*;

public class TextureRendererTest extends Application {

    public TextureRendererTest(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }
    Window window;
    TextureRenderer renderer;
    Texture texture;
    TextureRegion[][] regions;
    Camera camera;

    @Override
    public void setup() {
        int width=800,height=600;
        window=new Window(width,height,"Texture renderer test");
        camera=new Camera();
        camera.setPosition(width/2,height/2,0);
        camera.setScreenSize(width,height);
        camera.setScale(1,-1,1);
        camera.update();
        texture=new Texture("assets/examples/textureTest/tiles.png");
//        texture.setSamplerFilter(GL_LINEAR,GL_LINEAR);
        regions=TextureRegion.split(texture,18,18);
        renderer=new TextureRenderer();
        renderer.setCamera(camera);
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();

        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);

        renderer.draw(regions[0][0],500,500,20,20);
        renderer.draw(regions[4][0],100,100,200,200);
        renderer.flush();

        window.swapBuffers();
    }
}
