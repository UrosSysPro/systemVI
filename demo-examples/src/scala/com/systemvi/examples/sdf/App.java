package com.systemvi.examples.sdf;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.camera.CameraController3;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.texture.TextureData;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
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
    Camera3 camera, worldCamera;
    TextureRenderer renderer;
    final float Epsilon = 0.001f;
    final float focalLength=2.2f;
    public Random r=new Random();
    public TextureData data;
    public CameraController3 controller;
    public final int tasks=8,threads=8;
    ExecutorService service;
    Future[] futures;
    public RayMarchRenderer rayMarchRenderer;

    public Vector3f RayMarch(Vector3f ro, Vector3f rd,int iterations){
        float d = 0;
        for (int k = 0; k < iterations; k++) {
            Vector3f p = new Vector3f(
                ro.x + rd.x * d,
                ro.y + rd.y * d,
                ro.z + rd.z * d
            );
            d += Map.getDistance(p);
            if (d > 1000) break;
            if (Map.getDistance(p) < Epsilon) break;
        }
        return new Vector3f(
            ro.x + rd.x * d,
            ro.y + rd.y * d,
            ro.z + rd.z * d
        );
    }

    public Vector3f getNormal(Vector3f p){
        return new Vector3f(
            Map.getDistance(new Vector3f(p.x + 0.01f, p.y, p.z)) - Map.getDistance(new Vector3f(p.x - 0.01f, p.y, p.z)),
            Map.getDistance(new Vector3f(p.x, p.y + 0.01f, p.z)) - Map.getDistance(new Vector3f(p.x, p.y - 0.01f, p.z)),
            Map.getDistance(new Vector3f(p.x, p.y, p.z + 0.01f)) - Map.getDistance(new Vector3f(p.x, p.y, p.z - 0.01f))
        ).normalize();
    }

    public Vector4f SimulatePhoton(float x, float y, int bounces, Random r,int iterations){
        Vector4f color = new Vector4f(1);
        Vector3f[] ro = new Vector3f[bounces + 1];
        Vector3f[] rd = new Vector3f[bounces + 1];

        Matrix4f inverted=new Matrix4f(worldCamera.view()).invert();
        Vector4f focus=new Vector4f(0,0,focalLength,1).mul(inverted);
        Vector4f point=new Vector4f(x,y,0,1).mul(inverted);

        ro[0] = new Vector3f(focus.x,focus.y,focus.z);
        rd[0] = new Vector3f(point.x,point.y,point.z).sub(ro[0]).normalize();

        for (int k = 0; k < bounces; k++) {
            Vector3f p = RayMarch(ro[k], rd[k],iterations);
            Vector3f normal = getNormal(p);
            Material m = Map.getMaterial(p);
            Vector4f c = m.color();

            if (p.distance(ro[k]) > 1000)
                break;

            ro[k + 1] = new Vector3f(p).add(normal.x * 2 * Epsilon, normal.y * 2 * Epsilon, normal.z * 2 * Epsilon);
            rd[k + 1] = new Vector3f(rd[k]).reflect(normal).add(new Vector3f(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1).mul(m.roughness())).normalize();
            if (r.nextFloat() < m.metallic())
                color.mul(c);
        }
        return color;
    }
    public Vector4f calculatePixel(int i,int j,int bounces,int samples,int iterations){
        float x, y;
        x = i;
        x /= texture.getWidth();
        x = x*2-1;
        x *= (float)texture.getWidth() / (float)texture.getHeight();
        y = j;
        y /= texture.getHeight();
        y = 2*y-1;
        y *= -1;

        Vector4f color = new Vector4f(0);

        for (int k = 0; k < samples; k++) {
            color.add(SimulatePhoton(x, y, bounces, r,iterations));
        }
        color.div(samples);
        return color;
    }

    @Override
    public void setup(Window window) {
        camera = Camera3.builder2d()
            .size(window.getWidth(), window.getHeight())
            .position((float)window.getWidth()/2,(float)window.getHeight()/2)
            .scale(1, -1)
            .build();

        worldCamera = Camera3.builder3d()
            .position(0,100,-400)
            .rotation(-0.3f,(float)Math.PI,0)
            .build();
        controller = CameraController3.builder()
            .window(window)
            .camera(worldCamera)
            .speed(50)
            .build();
//        setInputProcessor(controller);

        texture = new Texture(window.getWidth(), window.getHeight(), Format.RGBA);
        data = new TextureData(window.getWidth(), window.getHeight(), Format.RGBA);

        renderer = new TextureRenderer();
        renderer.view(camera.view());
        renderer.projection(camera.projection());

        service = Executors.newFixedThreadPool(threads);
        futures=new Future[tasks];

        rayMarchRenderer=RayMarchRenderer.apply(camera);

        startRender(4,10,500);
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0.4f, 0, 0, 0, Utils.Buffer.COLOR_BUFFER);
        System.out.printf("%4d \r",getFPS());
        controller.update(delta);

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
                    data.setPixel4f(x,y,rayMarchRenderer.calculatePixel(x,y,texture.getWidth(),texture.getHeight(),bounces,samples,iterations));
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
