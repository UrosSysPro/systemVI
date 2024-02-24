package com.systemvi.examples.compute.fluid;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Fluid extends Game {

    public Fluid() {
        super(4, 3, 60,512,512,"Fluid");
    }

    private Camera camera;
    private Shader klik;
//    private Shader shader;
    private TextureRenderer renderer;
    private Simulation simulation;
    private int width, height;
    private Vector2i mouse,previousMouse;
    boolean mouseDown;

    @Override
    public void setup(Window window) {
        mouse=new Vector2i();
        previousMouse=new Vector2i();

        width = 256;
        height = 256;

        camera = Camera.default2d(window);

        simulation = new Simulation(width, height);

        klik = Shader.builder().compute("assets/examples/compute/fluid/fill.glsl").build();
        if(!klik.isCompiled()){
            System.out.println(klik.getLog());
        }

//        shader= Shader.builder()
//            .fragment("assets/examples/compute/fluid/fragment.glsl")
//            .vertex("assets/renderer/textureRenderer/vertex.glsl")
//            .build();
//        if(!shader.isCompiled()){
//            System.out.println(shader.getLog());
//        }
        renderer = new TextureRenderer();
        renderer.setCamera(camera);
//        renderer.setShader(shader);

        mouseDown = false;
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0f,0.3f,0.9f,1.0f, Utils.Buffer.COLOR_BUFFER);

        if (mouseDown) {
            int size = 20;
            simulation.density.bindAsImage(0);
            simulation.u.bindAsImage(1);
            simulation.v.bindAsImage(2);
            klik.use();
            klik.setUniform("size", new Vector2i(width, height));
            klik.setUniform("deltaTime", delta);
            klik.setUniform("offset", new Vector2i(mouse).div((float) getWindow().getWidth()/simulation.width).sub(size/2,size/2));
            klik.setUniform("velocity", new Vector2f(previousMouse).sub(mouse.x,mouse.y).div(10));
            klik.dispatch(size, size, 1);
            Utils.barrier(Utils.Barrier.IMAGE_ACCESS);
        }

        simulation.update(delta);

        renderer.draw(simulation.density, 0, 0, simulation.width, simulation.height);
        renderer.flush();
        renderer.draw(simulation.u, 256, 0, simulation.width, simulation.height);
        renderer.flush();
        renderer.draw(simulation.v, 256, 256, simulation.width, simulation.height);
        renderer.flush();

        System.out.print("\rFPS: " + getFPS());
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
    public boolean mouseMove(double x, double y) {
        previousMouse.set(mouse);
        mouse.set((int) x, (int) y);
        return true;
    }
}
