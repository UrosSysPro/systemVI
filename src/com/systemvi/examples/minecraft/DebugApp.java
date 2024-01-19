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
import com.systemvi.examples.minecraft.renderer.WorldRenderer;
import com.systemvi.examples.minecraft.world.World;


public class DebugApp extends Game {

    public DebugApp(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS,800,600,"Voxel world");
    }
    public CameraController2 controller;
    public Camera camera;
    public World world;
    public WorldRenderer worldRenderer;
    public TextureRenderer renderer;
    public Camera camera2d;
    @Override
    public void setup(Window window) {
        camera=Camera.default3d(window);

        controller=CameraController2.builder()
            .camera(camera)
            .flip(false,true)
            .speed(30)
            .build();
        setInputProcessor(controller);

        world=new World();

        camera2d=Camera.default2d(window,window.getWidth()/2,window.getHeight()/2,false);
        renderer=new TextureRenderer();
        renderer.setCamera(camera2d);

        window.addOnResizeListener((width, height) -> {
            camera2d.setPosition(width/2,height/2,0);
            camera2d.setScreenSize(width,height);
            camera2d.update();
        });

        worldRenderer=new WorldRenderer(window.getWidth(),window.getHeight());
    }

    @Override
    public void loop(float delta) {
        controller.update(delta);

        worldRenderer.render(world,camera);

        OpenGLUtils.clear(0,0,0,0, COLOR_BUFFER, DEPTH_BUFFER);
        Texture color=worldRenderer.color;
        Texture normal=worldRenderer.normal;

        renderer.draw(color,0,0,color.getWidth()/2,color.getHeight()/2);
        renderer.flush();
        renderer.draw(normal,normal.getWidth()/2,0,color.getWidth()/2,color.getHeight()/2);
        renderer.flush();
    }

}
