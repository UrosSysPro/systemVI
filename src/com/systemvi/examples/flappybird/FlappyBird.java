package com.systemvi.examples.flappybird;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.window.Window;
import static org.lwjgl.opengl.GL33.*;

public class FlappyBird extends Application {
    public Bird bird;
    public Wall[] walls;
    public Window window;
    public ShapeRenderer renderer;
    public Camera camera;

    public FlappyBird(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    @Override
    public void setup() {
        window=new Window(800,600,"Flappy Bird");
        renderer=new ShapeRenderer();
        camera=new Camera();
        camera.setPosition(400,300,0);
        camera.setScale(1,-1,1);
        camera.setScreenSize(800,600);
        camera.update();
        bird=new Bird();
        walls=new Wall[1];
        walls[0]=new Wall();
    }

    @Override
    public void loop(float delta) {
        //input
        if(window.shouldClose())close();
        window.pollEvents();

        //update
        for(Wall w:walls)w.update(delta);

        //draw
        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);

        renderer.setCamera(camera);
        bird.draw(renderer);
        for(Wall w:walls)w.draw(renderer);
        renderer.flush();

        window.swapBuffers();
    }
}
