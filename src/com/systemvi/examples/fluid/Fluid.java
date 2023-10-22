package com.systemvi.examples.fluid;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.window.Window;
import static org.lwjgl.opengl.GL33.*;

public class Fluid extends Application {

    public Fluid(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    Window window;
    Simulation simulation;
    ShapeRenderer renderer;
    Camera camera;
    float px,py,prevx,prevy;
    boolean mouseDown;
    int size;

    @Override
    public void setup() {
        int width=800;
        int height=800;
        window=new Window(width,height,"Fluid Sim");
        renderer=new ShapeRenderer();
        camera=new Camera();
        camera.setScale(1,-1,1);
        camera.setScreenSize(width,height);
        camera.setPosition(width/2,height/2,0);
        camera.update();
        renderer.setCamera(camera);
        size=4;
        System.out.println(width/size);
        simulation=new Simulation(width/size,height/size);

        window.addOnMouseMoveListener((x, y) -> {
            prevx=px;
            prevy=py;
            px= (float) x;
            py= (float) y;
        });
        window.addOnMouseUpListener((button, mods) -> {
            mouseDown=false;
        });
        window.addOnMouseDownListener((button, mods) -> {
            mouseDown=true;
        });
        window.addOnResizeListener((width1, height1) -> {
            camera.setPosition(width1/2,height1/2,0);
            camera.setScreenSize(width1,height1);
            camera.update();
        });
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();

        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);

        if(mouseDown){
            simulation.add((int) (px/size), (int) (py/size),size);
            simulation.addVelocity((int) (px/size), (int) (py/size),px-prevx,py-prevy,size);
        }

        simulation.dens_step(0.000f,delta);
        simulation.vel_step(0.0f,delta);

        simulation.draw(renderer,size);
        renderer.flush();

        window.swapBuffers();
    }
}
