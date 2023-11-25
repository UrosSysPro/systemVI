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
                mat[i][j]=new Cell(1,0);
                tmp[i][j]=new Cell(1,0);
            }
        }
        mat[10][10].b=1;
        tmp[10][10].b=1;

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
                if(i>=0&&i<mat.length&&j>=0&&j<mat[0].length&&mouseDown){
                    mat[i][j].b=1;
//                    mat[i][j].a+=0.1;
                }
            }
        }

        float[][] weights=new float[][]{
            {0.05f,   .2f, 0.05f},
            {   .2f,  -1,    .2f},
            {0.05f,   .2f, 0.05f},
        };
        float sum=0;
        for(int i=0;i<weights.length;i++){
            for(int j=0;j<weights.length;j++){
                sum+=weights[i][j];
            }
        }

        for(int i=0;i<mat.length;i++){
            for(int j=0;j<mat[0].length;j++){
                float a=mat[i][j].a,b=mat[i][j].b;
                float da=1f;
                float db=0.5f;
                float f=0.055f, k=0.062f;
                float pa=prosekA(weights,sum,i,j);
                float pb=prosekB(weights,sum,i,j);
                tmp[i][j].a=a+(da*pa-a*b*b+f*(a-1))*1;
                tmp[i][j].b=b+(db*pb+a*b*b-(k+f)*b)*1;
            }
        }
        Cell[][] p=mat;
        mat=tmp;
        tmp=p;

        for(int i=0;i<mat.length;i++){
            for(int j=0;j<mat[0].length;j++) {
                renderer.rect(
                    i*cellSize,
                    j*cellSize,
                    cellSize,
                    cellSize,
                    new Vector4f(mat[i][j].b)
                );
            }
        }
//        renderer.rect(0,0,100,100,new Vector4f(0.3f,0.6f,0.8f,1.0f));
        renderer.flush();
        window.swapBuffers();
    }
    public Cell get(int i,int j){
        if(i<0)i=mat.length-1;
        if(i>=mat.length)i=0;
        if(j<0)j=mat[0].length-1;
        if(j>=mat[0].length)j=0;
        return mat[i][j];
    }
    public float prosekA(float[][] weights,float sum,int x,int y){
        float prosek=0;
        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                prosek+=get(x+i,y+j).a*weights[i+1][j+1];
            }
        }
        return prosek/sum-get(x,y).a;
    }
    public float prosekB(float[][] weights,float sum,int x,int y){
        float prosek=0;
        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                prosek+=get(x+i,y+j).b*weights[i+1][j+1];
            }
        }
        return prosek/sum-get(x,y).b;
    }
}
