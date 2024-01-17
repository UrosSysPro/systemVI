package com.systemvi.examples.texturetest;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.SkyBoxRenderer;
import com.systemvi.engine.texture.CubeMap;
import com.systemvi.engine.utils.OpenGLUtils;
import com.systemvi.engine.window.Window;
import com.systemvi.examples.test3d.CameraController;

public class CubeMapTest extends Application {
    public CubeMapTest(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }
    public Window window;
    public CubeMap cubeMap;
    public SkyBoxRenderer renderer;
    public Camera camera;
    public CameraController controller;

    @Override
    public void setup() {
        int width=800,height=600;
        window=new Window(width,height,"Cube map test");
        cubeMap=new CubeMap(new String[]{
            "assets/examples/test3d/cube/texture.png",
            "assets/examples/test3d/cube/texture.png",
            "assets/examples/test3d/cube/texture.png",
            "assets/examples/test3d/cube/texture.png",
            "assets/examples/test3d/cube/texture.png",
            "assets/examples/test3d/cube/texture.png",
        });
        renderer=new SkyBoxRenderer(cubeMap);
        camera=new Camera();
        camera.setPerspectiveProjection((float) Math.toRadians(60), (float) width /height,0.1f,1000f);
        camera.update();
        controller=new CameraController(0,0,0,0,0,0);
        controller.camera=camera;

        window.addOnKeyPressListener((key, scancode, mods) -> controller.keyDown(key));
        window.addOnKeyReleaseListener((key, scancode, mods) -> controller.keyUp(key));
        window.addOnMouseDownListener((button, mods) -> controller.mouseDown());
        window.addOnMouseUpListener((button, mods) -> controller.mouseUp());
        window.addOnMouseMoveListener((x, y) -> controller.mouseMove((float)x,(float)(height-y)));
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();
        controller.update(delta);
        OpenGLUtils.clear(0.3f,0.6f,0.9f,1.0f, OpenGLUtils.Buffer.COLOR_BUFFER, OpenGLUtils.Buffer.DEPTH_BUFFER);



        OpenGLUtils.enableDepthTest();
        renderer.draw(camera);
        OpenGLUtils.disableDepthTest();

        window.swapBuffers();
    }
}
