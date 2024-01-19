package com.systemvi.engine.camera;

import com.systemvi.engine.window.Window;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {
    private final Matrix4f rotation,scale,translate,screenSize,view,projection;
    public Camera(){
        projection=new Matrix4f().identity();
        screenSize=new Matrix4f().identity();
        view=new Matrix4f().identity();
        rotation=new Matrix4f().identity();
        scale=new Matrix4f().identity();
        translate=new Matrix4f().identity();
    }
    public void setOrthographicProjection(float left, float right,float top,float bottom,float near, float far){
        projection.identity().ortho(left,right,bottom,top,near,far);
    }

    public void setPerspectiveProjection(float fov,float aspect,float near,float far){
        projection.identity().perspective(fov,aspect,near,far);
    }
    public Matrix4f getProjection() {
        return projection;
    }

    public void setPosition(float x, float y, float z){
        translate.identity().translate(-x,-y,-z);
    }
    public void setScale(float x,float y,float z){
        scale.identity().scale(1f/x,1f/y,1f/z);
    }
    public void setScreenSize(int width,int height){
        screenSize.identity().scale(2f/width,2f/height,1.0f);
    }

    public void setRotation(float x,float y,float z){
        rotation.identity().rotateXYZ(-x,-y,-z);
    }
    public void lookAt(Vector3f dir){
        rotation.identity().lookAlong(dir,new Vector3f(0,1,0));
    }
    public void lookAt(Vector3f dir,Vector3f up){
        rotation.identity().lookAlong(dir,up);
    }
    public void update(){
        view.identity().mul(scale).mul(screenSize).mul(rotation).mul(translate);
    }

    public Matrix4f getView() {
        return view;
    }

    public static Camera default2d(Window window,float x,float y, boolean flipY){
        Camera camera=new Camera();
        camera.setScreenSize(window.getWidth(),window.getHeight());
        camera.setPosition(x,y,0);
        camera.setScale(1,flipY?-1:1,1);
        camera.update();
        return camera;
    }
    public static Camera default2d(Window window){
        return default2d(window,window.getWidth()/2,window.getHeight()/2,true);
    }
}
