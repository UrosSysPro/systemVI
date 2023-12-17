package com.systemvi.examples.minecraft;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.utils.OpenGLUtils;
import com.systemvi.engine.utils.OpenGLUtils.Buffer;
import com.systemvi.engine.window.Window;
import com.systemvi.examples.minecraft.world.World;
import com.systemvi.examples.test3d.CameraController;

import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

public class DebugApp extends Application {

    public DebugApp(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    public Window window;
    public CameraController controller;
    public Camera camera;
    public World world;

    @Override
    public void setup() {

        float width=800,height=600;
        glfwWindowHint(GLFW_SAMPLES,3);
        window=new Window(800,600,"Debug Minecraft");

        camera=new Camera();
        camera.setPerspectiveProjection((float)Math.toRadians(60),width/height,0.1f,1000);
//        camera.setOrthographicProjection(-width/height,width/height,height/height,-height/height,0.1f,100);
        camera.update();

        controller=new CameraController(0,0,0,0,0,-(float)Math.PI/2);
        controller.camera=camera;
        window.addOnKeyPressListener((key, scancode, mods) -> controller.keyDown(key));
        window.addOnKeyReleaseListener((key, scancode, mods) -> controller.keyUp(key));
        window.addOnMouseMoveListener((x1, y1) -> controller.mouseMove((float) x1, 600-(float) y1));
        window.addOnMouseDownListener((button, mods) -> controller.mouseDown());
        window.addOnMouseUpListener((button, mods) -> controller.mouseUp());
        world=new World();
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();

        OpenGLUtils.clear(0,0,0,0,
            Buffer.COLOR_BUFFER,
            Buffer.DEPTH_BUFFER
        );
        OpenGLUtils.enableDepthTest();
        OpenGLUtils.enableFaceCulling();

        controller.update(delta);

        world.debugDraw(controller);

        OpenGLUtils.disableDepthTest();
        OpenGLUtils.disableFaceCulling();

        window.swapBuffers();
    }
}
