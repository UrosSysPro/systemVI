package com.systemvi.examples.inversekinematics;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.window.Window;
import org.joml.Vector4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL33.*;

public class Fabrik extends Application {

    public Fabrik(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }
    public Window window;

    public int n;
    public ArrayList<Vector> points;
    public ArrayList<Float> lengths;

    public ShapeRenderer renderer;
    public Camera camera;
    public float x,y;

    @Override
    public void setup() {
        window=new Window(800,600,"Fabrik");
        renderer=new ShapeRenderer();
        camera=new Camera();
        camera.setScreenSize(800,600);
        camera.setPosition(400,300,0);
        camera.setScale(1,-1,1);
        camera.update();
        renderer.setCamera(camera);

        n=3;
        points=new ArrayList<>(n);
        lengths=new ArrayList<>(n-1);
        float length=200;
        for(int i=0;i<n;i++){
            points.add(new Vector(400+i*(length/n),300));
        }
        for(int i=0;i<n-1;i++){
            lengths.add(length/n);
        }
        window.addOnMouseMoveListener((x1, y1) -> {
            x = (float) x1;
            y = (float) y1;
        });
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        window.pollEvents();
        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);

        Vector start=new Vector(points.get(0));

        //od kraja do pocetka
        points.get(n-1).set(x,y);
        for(int i=n-1;i>0;i--){
            Vector t=points.get(i);
            Vector p=points.get(i-1);
            Vector diff=new Vector(p).sub(t).normalize().multiply(lengths.get(i-1));
            p.set(t).add(diff);
        }
        //od pocetka do kraja
        points.get(0).set(start);
        for(int i=0;i<n-1;i++){
            Vector t=points.get(i);
            Vector s=points.get(i+1);
            Vector diff=new Vector(s).sub(t).normalize().multiply(lengths.get(i));
            s.set(t).add(diff);
        }


        for(int i=0;i<n-1;i++){
            Vector s=points.get(i);
            Vector e=points.get(i+1);
            float x=(s.x+e.x)/2;
            float y=(s.y+e.y)/2;
            float l=lengths.get(i);
            float lineWidth=3;
            x-=l/2;
            y-=lineWidth/2;
            float angle=(float)-Math.atan2(-s.x+e.x,-s.y+e.y)+(float)Math.PI/2;
            renderer.rect(x,y,l,lineWidth,new Vector4f(1),angle);
        }

        for(int i=0;i<n;i++){
            int size=5;
            renderer.rect(
                points.get(i).x-size/2,
                points.get(i).y-size/2,
                size,
                size,
                new Vector4f(0.4f,0.7f,0.2f,1.0f)
            );
        }
        renderer.flush();

        window.swapBuffers();
    }
}
