package com.systemvi.examples.minecraft;

import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.shader.Primitive;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.window.InputProcessor;
import org.joml.Vector3f;

public class Player implements InputProcessor {
    public Camera camera;
    public int health,hunger;
    public Vector3f position;
    public float yaw,pitch,roll;
    public boolean mouseDown=false;
    public Shader shader;
    public Player(Vector3f position){
        this.position=position;
        yaw=0;
        pitch=0;
        roll=0;
        shader= Shader.builder()
            .fragment("assets/examples/minecraft/player/fragment.glsl")
            .vertex("assets/examples/minecraft/player/vertex.glsl")
            .build();
    }
    public void update(){

    }
    public void draw(Camera camera){
        shader.use();
        shader.setUniform("projection",camera.getProjection());
        shader.setUniform("view",camera.getView());
        shader.setUniform("position",position);
        shader.drawArrays(Primitive.TIRANGLE_STRIP,4);
    }

    @Override
    public boolean keyDown(int key, int scancode, int mods) {
        return false;
    }

    @Override
    public boolean keyUp(int key, int scancode, int mods) {
        return false;
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
        if(mouseDown){

        }
        return false;
    }

    @Override
    public boolean scroll(double offsetX, double offsetY) {
        return false;
    }

    @Override
    public boolean resize(int width, int height) {
        return false;
    }
}
