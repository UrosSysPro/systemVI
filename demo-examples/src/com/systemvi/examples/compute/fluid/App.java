package com.systemvi.examples.compute.fluid;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class App extends Game {

    public App() {
        super(4, 6, 60,800,800,"Fluid");
    }

    private Camera camera;
    private TextureRenderer renderer;
    private Simulation simulation;
    private int width, height;
    private Vector2i mouse,previousMouse;
    boolean leftClick=false,rightClick=false;

    @Override
    public void setup(Window window) {
        mouse=new Vector2i();
        previousMouse=new Vector2i();

        width = window.getWidth();
        height = window.getHeight();

        camera = Camera.default2d(window);

        simulation = new Simulation(width, height);

        renderer = new TextureRenderer();
        renderer.setCamera(camera);
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0f,0.3f,0.9f,1.0f, Utils.Buffer.COLOR_BUFFER);

        if (leftClick||rightClick) {
            int size=50;
            int scale=1;
            int x=mouse.x/scale;
            int y=mouse.y/scale;
            int px=previousMouse.x/scale;
            int py=previousMouse.y/scale;
            simulation.add(x,y,px,py,delta,size,leftClick?1:0,1f);
            simulation.project();
        }

        simulation.update(delta);

        renderer.draw(simulation.density, 0, 0, simulation.width, simulation.height);
        renderer.flush();

        System.out.print("\rFPS: " + getFPS());
    }
    @Override
    public boolean mouseDown(int button, int mods, double x, double y) {
        if(button== GLFW.GLFW_MOUSE_BUTTON_1)leftClick=true;
        if(button== GLFW.GLFW_MOUSE_BUTTON_2)rightClick=true;
        return true;
    }
    @Override
    public boolean mouseUp(int button, int mods, double x, double y) {
        if(button== GLFW.GLFW_MOUSE_BUTTON_1)leftClick=false;
        if(button== GLFW.GLFW_MOUSE_BUTTON_2)rightClick=false;
        return true;
    }
    @Override
    public boolean mouseMove(double x, double y) {
        previousMouse.set(mouse);
        mouse.set((int) x, (int) y);
        return true;
    }
}
