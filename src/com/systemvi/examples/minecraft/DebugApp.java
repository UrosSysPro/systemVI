package com.systemvi.examples.minecraft;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.camera.CameraController2;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.FrameBuffer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.OpenGLUtils;
import static com.systemvi.engine.utils.OpenGLUtils.Buffer.*;
import com.systemvi.engine.window.Window;
import com.systemvi.examples.minecraft.world.World;

import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

public class DebugApp extends Game {

    public DebugApp(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS,800,600,"Voxel world");
    }
    public CameraController2 controller;
    public Camera camera;
    public World world;
    public Texture colorTexture,colorTexture2,depth;
    public FrameBuffer frameBuffer,frameBuffer2;
    public TextureRenderer renderer;
    public Camera camera2d;
    @Override
    public void setup(Window window) {

        float width=800,height=600;
        glfwWindowHint(GLFW_SAMPLES,3);

        camera=Camera.default3d(window);

        controller=CameraController2.builder()
            .camera(camera)
            .flip(false,true)
            .speed(30)
            .build();
        setInputProcessor(controller);

        world=new World();

        colorTexture=new Texture(800,600, Format.RGBA);
        colorTexture2=new Texture(800,600,Format.RG);
        depth=Texture.depth(800,600,Format.DEPTH);
        frameBuffer=FrameBuffer.builder()
            .color(colorTexture)
            .depth(depth)
            .build();
        frameBuffer2=FrameBuffer.builder()
            .color(colorTexture2)
            .depthAndStencil(true)
            .build();
        camera2d=Camera.default2d(window,window.getWidth()/2,window.getHeight()/2,false);
        renderer=new TextureRenderer();
        renderer.setCamera(camera2d);
    }

    @Override
    public void loop(float delta) {

        controller.update(delta);

        frameBuffer.begin();
        frameBuffer2.begin();
        drawWorld();
        frameBuffer2.end();
        drawWorld();
        frameBuffer.end();

        OpenGLUtils.clear(0,0,0,0, COLOR_BUFFER, DEPTH_BUFFER);
        renderer.draw(colorTexture,0,0,colorTexture.getWidth()/2,colorTexture.getHeight()/2);
        renderer.flush();

        renderer.draw(colorTexture2,colorTexture2.getWidth()/2,0,colorTexture.getWidth()/2,colorTexture.getHeight()/2);
        renderer.flush();

        renderer.draw(depth,0,colorTexture2.getHeight()/2,colorTexture.getWidth()/2,colorTexture.getHeight()/2);
        renderer.flush();

    }
    public void drawWorld(){
        OpenGLUtils.clear(0,0,0,0, COLOR_BUFFER,DEPTH_BUFFER);

        OpenGLUtils.enableDepthTest();
        OpenGLUtils.enableFaceCulling();

        world.draw(controller);
//        world.debugDraw(controller);

        OpenGLUtils.disableDepthTest();
        OpenGLUtils.disableFaceCulling();
    }
}
