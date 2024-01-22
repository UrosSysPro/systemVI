package com.systemvi.examples.minecraft;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.camera.CameraController2;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.FrameBuffer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.OpenGLUtils;
import static com.systemvi.engine.utils.OpenGLUtils.Buffer.*;
import com.systemvi.engine.window.Window;
import com.systemvi.examples.minecraft.materials.Material;
import com.systemvi.examples.minecraft.renderer.WorldRenderer;
import com.systemvi.examples.minecraft.world.World;
import org.joml.Vector3f;


public class DebugApp extends Game {

    public DebugApp(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS,800,600,"Voxel world");
    }
    public CameraController2 controller;
    public Camera camera;
    public World world;
    public WorldRenderer worldRenderer;
    public TextureRenderer renderer;
    public Shader depthShader,positionShader,finalGather;
    public Camera camera2d;
    public Material material;
    public Window mainWindow,secondWindow;
    @Override
    public void setup(Window window) {
        mainWindow=window;
//        secondWindow=new Window(window.getWidth(),window.getHeight(),"Combined");
        material=new Material();
        camera=Camera.default3d(window);

        controller=CameraController2.builder()
            .camera(camera)
            .flip(false,true)
            .speed(10)
            .build();
        mainWindow.setInputProcessor(controller);
//        secondWindow.setInputProcessor(controller);
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

        depthShader= Shader.builder()
            .fragment("assets/examples/minecraft/debug/depthShader.glsl")
            .vertex("assets/renderer/textureRenderer/vertex.glsl")
            .build();
        if(!depthShader.isCompiled()){
            System.out.println(depthShader.getLog());
        }
        positionShader= Shader.builder()
            .fragment("assets/examples/minecraft/debug/positionShader.glsl")
            .vertex("assets/renderer/textureRenderer/vertex.glsl")
            .build();
        if(!positionShader.isCompiled()){
            System.out.println(positionShader.getLog());
        }
        finalGather= Shader.builder()
                .fragment("assets/examples/minecraft/debug/finalGather.glsl")
                .vertex("assets/renderer/textureRenderer/vertex.glsl")
                .build();
        if(!positionShader.isCompiled()){
            System.out.println(positionShader.getLog());
        }
    }

    @Override
    public void loop(float delta) {
//        mainWindow.use();
        controller.update(delta);

        worldRenderer.render(world,camera,material);

        OpenGLUtils.clear(0,0,0,0, COLOR_BUFFER, DEPTH_BUFFER);
        Texture color=worldRenderer.color;
        Texture normal=worldRenderer.normal;
        Texture depth=worldRenderer.depth;
        Texture position=worldRenderer.position;
        int width=color.getWidth(),height=color.getHeight();

//        renderer.draw(color,0,0,width/2,height/2);
//        renderer.flush();

//        renderer.draw(normal,0,0,width,height);
//        renderer.flush();

        finalGather.use();
        normal.bind(1);
        position.bind(2);
        finalGather.setUniform("normalBuffer",1);
        finalGather.setUniform("positionBuffer",2);
        finalGather.setUniform("cameraPosition",new Vector3f(
                controller.x,controller.y,controller.z
        ));
        finalGather.setUniform("lightPosition",new Vector3f(20,100,20));
        renderer.setShader(finalGather);
        renderer.draw(color,0,0,width,height);
        renderer.flush();
        renderer.setShader(null);

//        renderer.setShader(depthShader);
//        renderer.draw(depth,0,600-height/2, width/2,height/2 );
//        renderer.flush();
//        renderer.setShader(null);

//        renderer.setShader(positionShader);
//        renderer.draw(position,width/2,height/2,width/2,height/2);
//        renderer.flush();
//        renderer.setShader(null);

//        secondWindow.use();
    }

}
