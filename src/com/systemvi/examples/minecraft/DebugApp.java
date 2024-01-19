package com.systemvi.examples.minecraft;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.FrameBuffer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.OpenGLUtils;
import static com.systemvi.engine.utils.OpenGLUtils.Buffer.*;
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
    public Texture colorTexture,colorTexture2,depth;
    public FrameBuffer frameBuffer,frameBuffer2;
    public TextureRenderer renderer;
    public Camera camera2d;
    @Override
    public void setup() {

        float width=800,height=600;
        glfwWindowHint(GLFW_SAMPLES,3);
        window=new Window(800,600,"Debug Minecraft");

        camera=new Camera();
        camera.setPerspectiveProjection((float)Math.toRadians(60),width/height,0.1f,1000);
//        float aspect=width/height;
//        float viewportScale=50;
//        camera.setOrthographicProjection(-aspect*viewportScale,aspect*viewportScale,viewportScale,-viewportScale,0.1f,1000);
        camera.update();

        controller=new CameraController(0,40,0,0,0,-(float)Math.PI/2);
        controller.speed=30;
        controller.camera=camera;
        window.addOnKeyPressListener((key, scancode, mods) -> controller.keyDown(key));
        window.addOnKeyReleaseListener((key, scancode, mods) -> controller.keyUp(key));
        window.addOnMouseMoveListener((x1, y1) -> controller.mouseMove((float) x1, 600-(float) y1));
        window.addOnMouseDownListener((button, mods) -> controller.mouseDown());
        window.addOnMouseUpListener((button, mods) -> controller.mouseUp());
        window.addOnResizeListener((width1, height1) -> {
            camera.setPerspectiveProjection((float)Math.toRadians(60),(float)width1/(float)height1,0.1f,1000);
//        camera.setOrthographicProjection(-width/height,width/height,height/height,-height/height,0.1f,100);
            camera.update();
        });
        world=new World();

        colorTexture=new Texture(800,600, Format.RGBA);
        colorTexture2=new Texture(800,600,Format.RG);
        depth=Texture.depth(800,600);
        frameBuffer=FrameBuffer.builder()
            .color(colorTexture)
//            .depthAndStencil(true)
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
        if(window.shouldClose())close();
        window.pollEvents();

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

        window.swapBuffers();
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
