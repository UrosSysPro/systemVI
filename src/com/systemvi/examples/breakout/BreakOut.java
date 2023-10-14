package com.systemvi.examples.breakout;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.window.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL33.*;

public class BreakOut extends Application {
    public BreakOut(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }
    Window window;
    ShapeRenderer renderer;
    Camera camera;
    Player player;
    Wall[][] walls;
    Ball ball;

    @Override
    public void setup() {
        window=new Window(800,600,"Break Out");
        renderer=new ShapeRenderer();
        camera=new Camera();
        camera.setPosition(window.getWidth()/2,window.getHeight()/2,0);
        camera.setScale(1,-1,1);
        camera.setScreenSize(window.getWidth(),window.getHeight());
        camera.update();
        window.addOnResizeListener((width, height) -> {
            camera.setPosition(window.getWidth()/2,window.getHeight()/2,0);
            camera.setScreenSize(window.getWidth(),window.getHeight());
            camera.update();
        });
        window.addOnMouseMoveListener((x, y) -> {
            player.x=(int)x-player.width/2;
            if(player.x<0)player.x=0;
            if(player.x>window.getWidth()-player.width)
                player.x=window.getWidth()-player.width;
        });

        player=new Player(window.getWidth(),window.getHeight());
        walls=new Wall[10][5];
        Vector4f[] colors=new Vector4f[]{
            new Vector4f(1,0,0,1),
            new Vector4f(1,0,0.5f,1),
            new Vector4f(0.7f,0,0.5f,1),
            new Vector4f(0.3f,1,0.3f,1),
            new Vector4f(0.3f,0.3f,1f,1),
        };
        for(int i=0;i<walls.length;i++){
            for(int j=0;j<walls[i].length;j++){
                walls[i][j]=new Wall(i*Wall.width,j*Wall.height,colors[j]);
            }
        }
        ball=new Ball(window.getWidth(),window.getHeight());
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();

        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);

        ball.update(delta,walls,window,player);

        renderer.setCamera(camera);
        for(int i=0;i<walls.length;i++){
            for(int j=0;j<walls[i].length;j++){
                if(walls[i][j].visible)
                    walls[i][j].draw(renderer);
            }
        }
        player.draw(renderer);
        ball.draw(renderer);
        renderer.flush();

        window.swapBuffers();
    }
}
