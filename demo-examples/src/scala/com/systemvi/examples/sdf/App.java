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
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class App extends Game {
    public App(int openglVersionMajor, int openglVersionMinor, int targetFPS, int windowWidth, int windowHeight, String title) {
        super(openglVersionMajor, openglVersionMinor, targetFPS, windowWidth, windowHeight, title);
    }

    public App(){
        this(3, 3, 60, 800, 600, "SDFs");
    }

    Texture texture;
    Camera3 camera, worldCamera;
    TextureRenderer renderer;
    final float Epsilon = 0.001f;
    public Random r=new Random();
    public TextureData data;
    public CameraController3 controller;

    public float Map(Vector3f p){
        return SDF.Union(SDF.Sphere(
                SDF.Translate(p, new Vector3f(0, 0, -200)),
                100
            ), SDF.Sphere(
                SDF.Translate(p, new Vector3f(-100, 200, -300)),
                100),
            SDF.Plain(SDF.Translate(p, new Vector3f(0, -100, 0))));
    }

    public Vector3f RayMarch(Vector3f ro, Vector3f rd){
        float d = 0;
        for (int k = 0; k < 100; k++) {
            Vector3f p = new Vector3f(
                ro.x + rd.x * d,
                ro.y + rd.y * d,
                ro.z + rd.z * d
            );
            d += Map(p);
            if (d > 1000) break;
            if (Map(p) < Epsilon) break;
        }
        return new Vector3f(
            ro.x + rd.x * d,
            ro.y + rd.y * d,
            ro.z + rd.z * d
        );
    }

    public Vector3f getNormal(Vector3f p){
        return new Vector3f(
            Map(new Vector3f(p.x + 0.01f, p.y, p.z)) - Map(new Vector3f(p.x - 0.01f, p.y, p.z)),
            Map(new Vector3f(p.x, p.y + 0.01f, p.z)) - Map(new Vector3f(p.x, p.y - 0.01f, p.z)),
            Map(new Vector3f(p.x, p.y, p.z + 0.01f)) - Map(new Vector3f(p.x, p.y, p.z - 0.01f))
        ).normalize();
    }

    public Material getMaterial(Vector3f p){
        if (SDF.Sphere(
            SDF.Translate(p, new Vector3f(0, 0, -200)),
            100

        ) < Epsilon)
            return new Material(0.3f, 1f, new Vector4f(0.7f, 0.4f, 0.4f, 0));

        if (SDF.Sphere(
            SDF.Translate(p, new Vector3f(-100, 200, -300)),
            100
        ) < Epsilon)
            return new Material(0.6f, 0f, new Vector4f(0.3f, 0.9f, 0.5f, 0));

        if (SDF.Plain(SDF.Translate(p, new Vector3f(0, -100, 0))) < Epsilon)
            return new Material(0.8f, 0f, new Vector4f(0.7f, 0.7f, 0.3f, 1.0f));

        return new Material(0f, 0f, new Vector4f(1f));
    }

    public Vector4f SimulatePhoton(float x, float y, int n, Random r){
        Vector4f color = new Vector4f(1);
        Vector3f[] ro = new Vector3f[n + 1];
        Vector3f[] rd = new Vector3f[n + 1];

        Matrix4f inverted=new Matrix4f(worldCamera.view()).invert();
        Vector4f focus=new Vector4f(0,0,0.7f,1).mul(inverted);
        Vector4f point=new Vector4f(x,y,0,1).mul(inverted);

        ro[0] = new Vector3f(focus.x,focus.y,focus.z);
        rd[0] = new Vector3f(point.x,point.y,point.z).sub(ro[0]).normalize();

        for (int k = 0; k < n; k++) {
            Vector3f p = RayMarch(ro[k], rd[k]);
            Vector3f normal = getNormal(p);
            Material m = getMaterial(p);
            Vector4f c = m.color;

            if (p.distance(ro[k]) > 1000)
                break;

            ro[k + 1] = new Vector3f(p).add(normal.x * 2 * Epsilon, normal.y * 2 * Epsilon, normal.z * 2 * Epsilon);
            rd[k + 1] = new Vector3f(rd[k]).reflect(normal).add(new Vector3f(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1).mul(m.roughness)).normalize();
//            rd[k + 1] = new Vector3f(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1).normalize();
//            if (rd[k + 1].dot(normal) < 0)
//                rd[k + 1].mul(-1);
            if (r.nextFloat() < m.metallic || k == 0)
                color.mul(c);
        }
        return color;
    }
    public Vector4f calculatePixel(int i,int j){
        float x, y;
        x = i;
        x /= 800;
        x = x*2-1;
        x *= 800f / 600f;
        y = j;
        y /= 600;
        y = 2*y-1;
        y *= -1;

        Vector4f color = new Vector4f(0);

        int bounces = 7, photons = 10;

        for (int k = 0; k < photons; k++) {
            color.add(SimulatePhoton(x, y, bounces, r));
        }
        color.div(photons);
        return color;
    }

    @Override
    public void setup(Window window) {
        camera = Camera3.builder2d().size(800, 600).position(400, 300).scale(1, -1).build();

        worldCamera = Camera3.builder3d()
            .build();
        controller = CameraController3.builder()
            .window(window)
            .camera(worldCamera)
            .build();
        setInputProcessor(controller);

        texture = new Texture(800, 600, Format.RGBA);
        data = new TextureData(800, 600, Format.RGBA);
        renderer = new TextureRenderer();
        renderer.view(camera.view());
        renderer.projection(camera.projection());
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0.4f, 0, 0, 0, Utils.Buffer.COLOR_BUFFER);
        System.out.printf("%4d \r",getFPS());
        controller.update(delta);

        for(int i=0;i<1000;i++){
            int x=r.nextInt(800),y=r.nextInt(600);
            try{
                data.setPixel4f(x,y,calculatePixel(x,y));
            }catch (Exception e){
                System.out.println(x+" "+y);
                close();
                break;
            }
        }
        texture.setData(data);
        renderer.draw(texture, 0, 0, 800, 600);
        renderer.flush();
    }
}
