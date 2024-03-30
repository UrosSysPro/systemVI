package com.systemvi.engine.camera;

import org.joml.Matrix4f;

public class OrthographicCamera {
    private Matrix4f view, rotation, scale, translate;
    public OrthographicCamera(){
        view = new Matrix4f().identity();
        rotation = new Matrix4f().identity();
        scale = new Matrix4f().identity();
        translate = new Matrix4f().identity();
    }
    public void setPosition(float x, float y, float z){
        translate.identity().translate(-x, -y, -z);
    }
    public void setScreenSize(float width, float height){
        scale.identity().scale(2f / width, 2f / height, 1.0f);
    }
    public void setRotation(float x, float y, float z){
        rotation.identity().rotateXYZ(-x, -y, -z);
    }
    public void update(){
        view.identity().mul(rotation).mul(scale).mul(translate);
    }
    public Matrix4f getView() {
        return view;
    }
}
