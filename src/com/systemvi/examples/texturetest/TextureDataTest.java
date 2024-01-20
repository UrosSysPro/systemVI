package com.systemvi.examples.texturetest;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureData;
import com.systemvi.engine.utils.OpenGLUtils;
import com.systemvi.engine.window.Window;
import org.joml.Vector3i;

public class TextureDataTest extends Application {

    public TextureDataTest(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    public Window window;
    public Camera camera;
    public Texture texture;
    public TextureRenderer renderer;

    @Override
    public void setup() {
        window=new Window(800,600,"Texture Data Test");
        camera=new Camera();
        camera.setPosition(400,300,0);
        camera.setScreenSize(800,600);
        camera.setScale(1,-1,1);
        camera.update();
        renderer=new TextureRenderer();
        renderer.setCamera(camera);

        Vector3i color=new Vector3i();
        texture=new Texture(255,255, Format.RGBA);
//        TextureData data=new TextureData(255,255,Format.RGBA);
//        for(int i=0;i<data.getWidth();i++){
//            for(int j=0;j<data.getHeight();j++){
//                int repeat=1;
//                int width=256/repeat;
//                int r=(i%width)*(256/width);
//                int g=(j%width)*(256/width);
//                data.setPixel3i(i,j,color.set(r,g,128));
//            }
//        }
        TextureData data=new TextureData("assets/examples/test3d/rock/diffuse.png");
        texture.setData(data);
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();

//        OpenGLUtils.enableLines(4);
        OpenGLUtils.clear(0.3f,0.6f,0.9f,1.0f, OpenGLUtils.Buffer.COLOR_BUFFER, OpenGLUtils.Buffer.DEPTH_BUFFER);
        renderer.draw(texture,100,100, texture.getWidth(), texture.getHeight());
        renderer.flush();
//        OpenGLUtils.disableLines();

        window.swapBuffers();
    }
}
