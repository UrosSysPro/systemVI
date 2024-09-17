package com.systemvi.sdf.cpu;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureData;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import com.systemvi.sdf.cpu.maps.Map;
import com.systemvi.sdf.cpu.maps.Map2;
import com.systemvi.sdf.cpu.maps.Map3;
import org.joml.*;

import java.lang.Math;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class App extends Game {
    public App(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    public App(){
        this(3, 3, 60, 800, 600, "Ray Marching");
    }

    Texture texture;
    Camera3 camera;
    TextureRenderer renderer;
    public TextureData data;
    public final int tasks=8,threads=8;
    ExecutorService service;
    Future[] futures;
    public RayMarchRenderer rayMarchRenderer;

    @Override
    public void setup(Window window) {
        camera = Camera3.builder2d()
            .size(window.getWidth(), window.getHeight())
            .position((float)window.getWidth()/2,(float)window.getHeight()/2)
            .scale(1, -1)
            .build();

        texture = new Texture(window.getWidth(), window.getHeight(), Format.RGBA);
        data = new TextureData(window.getWidth(), window.getHeight(), Format.RGBA);

        renderer = new TextureRenderer();
        renderer.view(camera.view());
        renderer.projection(camera.projection());

        service = Executors.newFixedThreadPool(threads);
        futures=new Future[tasks];

        rayMarchRenderer = Map2.renderer();

        startRender(10,10,1000);
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0.4f, 0, 0, 0, Utils.Buffer.COLOR_BUFFER);
        System.out.printf("%4d \r",getFPS());

        texture.setData(data);
        renderer.draw(texture, 0, 0, texture.getWidth(), texture.getHeight());
        renderer.flush();
    }

    @Override
    public void dispose() {
        try{
            service.shutdown();
            service.awaitTermination(0, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startRender(int bounces,int samples,int iterations){
        int tasks=futures.length;
        for(int k=0;k<tasks;k++){
            final int index=k;
            futures[k]=service.submit(()->{
                int width=texture.getWidth()/tasks;
                int height=texture.getHeight();
                Vector2i[] indices=new Vector2i[width*height];
                java.util.Random random=new java.util.Random();
                for(int j=0;j<width*height;j++){
                    indices[j]=new Vector2i(j%width,j/width);
                }
                for(int j=0;j<width*height;j++){
                    int t=random.nextInt(width*height);
                    Vector2i tmp=indices[t];
                    indices[t]=indices[j];
                    indices[j]=tmp;
                }
                for(int j=0;j<width*height;j++){
                    int x=index*width+indices[j].x;
                    int y=indices[j].y;
                    Vector4f color=rayMarchRenderer.calculatePixel(x,y,texture.getWidth(), texture.getHeight(), bounces,samples,iterations);
                    float gamma=2.2f;
                    color.x=(float)Math.pow(color.x,1/gamma);
                    color.y=(float)Math.pow(color.y,1/gamma);
                    color.z=(float)Math.pow(color.z,1/gamma);
                    color.min(new Vector4f(1));
                    data.set(x,y,color);
                }
            });
        }
    }

    public void awaitRender(){
        int tasks=futures.length;
        try{
            for(int k=0;k<tasks;k++){futures[k].get();}
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
