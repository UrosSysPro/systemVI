package com.systemvi.examples.benchmark.shaperenderer;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.window.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL33.*;

public class App extends Application {
    public App(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }
    Window window;
    ShapeRenderer renderer;
    Camera camera;
    Screen terminal;
    TextGraphics graphics;
    int size=5;

    Vector4f color=new Vector4f();

    @Override
    public void setup() {
        window=new Window(800,600,"Shape Renderer BenchMark");
        renderer=new ShapeRenderer(2000,2000);
        camera=new Camera();
        camera.setScreenSize(window.getWidth(),window.getHeight());
        camera.setPosition(window.getWidth()/2,window.getHeight()/2,0);
        camera.setScale(1,-1,1);
        camera.update();
        renderer.setCamera(camera);

        try{
            terminal=new DefaultTerminalFactory().createScreen();
            graphics=terminal.newTextGraphics();
            terminal.startScreen();
        }catch (Exception e){
            e.printStackTrace();
        }

        window.addOnResizeListener((width,height)->{
            camera.setPosition(width/2,height/2,0);
            camera.setScreenSize(width,height);
            camera.update();
        });
        window.addOnKeyPressListener((key, scancode, mods) -> {
            System.out.println(key);
        });
        window.addOnKeyReleaseListener((key, scancode, mods) ->{
            System.out.println(key);
        });
        window.addOnMouseDownListener((button, mods) -> {
            System.out.println(button);
        });
        window.addOnMouseUpListener((button, mods) -> {
            System.out.println(button);
        });
        window.addOnMouseDownListener((button, mods) -> {
            System.out.println(button);
        });
        window.addOnScrollListener((xoffset, yoffset) -> {
            System.out.println(xoffset+" "+yoffset);
        });
        window.addOnScrollListener((xoffset, yoffset) -> {
            System.out.println(xoffset+" "+yoffset);
        });
    }
    @Override
    public void loop(float delta) {
        if(window.shouldClose()){
            close();
            try{
                window.close();
                terminal.stopScreen();
                terminal.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        window.pollEvents();

        glClearColor(0.3f,0.7f,0.2f,1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        for(int i=0;i<window.getWidth()/size;i++){
            for(int j=0;j<window.getHeight()/size;j++){
                color.set((float) i/(window.getWidth()/size),(float)j/(window.getHeight()/size),0.5f,1.0f);
                renderer.rect(i*size,j*size,size,size,color);
            }
        }
        renderer.flush();


        window.swapBuffers();

        try{
            terminal.clear();
            graphics.putString(0,0,"fps: "+getFPS());
            graphics.putString(0,1,"frame time: "+(double)getFrameTime()/1000_000_000d);
            graphics.putString(0,2,"width "+window.getWidth());
            graphics.putString(0,3,"height "+window.getHeight());
            graphics.putString(0,4,"rects "+window.getWidth()*window.getHeight()/(size*size));
            graphics.putString(0,5,"delta "+delta);
            terminal.refresh();
        }catch (Exception e){
            e.printStackTrace();
            close();
        }
    }
}
