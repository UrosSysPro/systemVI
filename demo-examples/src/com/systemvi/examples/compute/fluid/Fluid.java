package com.systemvi.examples.compute.fluid;


import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import static org.lwjgl.glfw.GLFW.*;
public class Fluid extends Game {
    public Fluid(){
        super(4,3,60,800,800,"Fluid");
    }
    Simulation simulation;
    ShapeRenderer renderer;
    Camera camera;
    float px,py,prevx,prevy;
    boolean mouseDown,spaceDown=false;
    int size;
    @Override
    public void setup(Window window) {
        int width=800;
        int height=800;
        renderer=new ShapeRenderer();
        camera=new Camera();
        camera.setScale(1,-1,1);
        camera.setScreenSize(width,height);
        camera.setPosition(width/2,height/2,0);
        camera.update();
        renderer.setCamera(camera);
        size=10;
        System.out.println(width/size);
        simulation=new Simulation(width/size,height/size);
    }
    @Override
    public void loop(float delta) {
        Utils.clear(0,0,0,1, Utils.Buffer.COLOR_BUFFER);

        if(mouseDown){
            simulation.add((int) (px/size), (int) (py/size),3);
            simulation.addVelocity((int) (px/size), (int) (py/size),px-prevx,py-prevy,3);
        }

        simulation.dens_step(0.000f,spaceDown?-delta:delta);
        simulation.vel_step(0.0f,spaceDown?-delta:delta);

        simulation.draw(renderer,size);
        renderer.flush();

    }
    @Override
    public boolean mouseMove(double x, double y) {
        prevx=px;
        prevy=py;
        px= (float) x;
        py= (float) y;
        return true;
    }
    @Override
    public boolean mouseDown(int button, int mods, double x, double y) {
        mouseDown=true;
        return true;
    }
    @Override
    public boolean mouseUp(int button, int mods, double x, double y) {
        mouseDown=false;
        return true;
    }

    @Override
    public boolean keyDown(int key, int scancode, int mods) {
        if(key==GLFW_KEY_SPACE)spaceDown=true;
        return true;
    }

    @Override
    public boolean keyUp(int key, int scancode, int mods) {
        if(key==GLFW_KEY_SPACE)spaceDown=false;
        return true;
    }

    @Override
    public boolean resize(int width, int height) {
        camera.setPosition(width/2,height/2,0);
        camera.setScreenSize(width,height);
        camera.update();
        return true;
    }
}
