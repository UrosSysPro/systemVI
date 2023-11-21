package com.systemvi.examples.reactiondiffusion;

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
    public Window window;
    public Camera camera;
    public ShapeRenderer renderer;
    public Cell[][] mat,tmp;
    public int cellSize,width,height,x,y;
    public boolean mouseDown;


    @Override
    public void setup() {
        width=800;
        height=600;
        window=new Window(width,height,"Reaction diffusion");
        camera=new Camera();
        camera.setScale(1,-1,1);
        camera.setPosition(width/2,height/2,0);
        camera.setScreenSize(width,height);
        camera.update();
        renderer=new ShapeRenderer();
        renderer.setCamera(camera);

        cellSize=10;
        mat=new Cell[width/cellSize][height/cellSize];
        tmp=new Cell[width/cellSize][height/cellSize];
        for(int i=0;i<mat.length;i++){
            for(int j=0;j<mat[0].length;j++){
                mat[i][j]=new Cell();
            }
        }
        mouseDown=false;
        window.addOnMouseMoveListener((x1, y1) -> {
            x=(int) x1/cellSize;
            y=(int) y1/cellSize;
        });
        window.addOnMouseUpListener((button, mods) -> mouseDown=false);
        window.addOnMouseDownListener((button, mods) -> mouseDown=true);
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();
        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);

        int spread=2;
        for(int i=x-spread;i<=x+spread;i++){
            for(int j=y-spread;j<=y+spread;j++){
                if(i>=0&&i<mat.length&&j>=0&&j<mat[0].length){

                }
            }
        }

        for(int i=0;i<mat.length;i++){
            for(int j=0;j<mat[0].length;j++){

            }
        }


        renderer.rect(0,0,100,100,new Vector4f(0.3f,0.6f,0.8f,1.0f));
        renderer.flush();
        window.swapBuffers();
    }
}
