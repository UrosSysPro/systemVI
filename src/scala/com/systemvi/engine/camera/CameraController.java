package com.systemvi.engine.camera;

import com.systemvi.engine.camera.Camera;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class CameraController {
    public Camera camera;
    public float x,y,z,roll,pitch,yaw;
    public float sensitivity,speed;
    public boolean forward,backwards,left,right,up,down,mouseDown;
    public float mx,my;

    public CameraController(float x,float y,float z,float roll,float pitch,float yaw){
        this.x=x;
        this.y=y;
        this.z=z;
        this.roll=roll;
        this.pitch=pitch;
        this.yaw=yaw;
        forward=false;
        backwards=false;
        left=false;
        right=false;
        down=false;
        up=false;
        mouseDown=false;
        mx=0;
        my=0;
        sensitivity=100;
        speed=1;
    }

    public CameraController(){
        this(0,0,0,0,0,0);
    }

    public void keyDown(int key){
        if(key==GLFW_KEY_W)forward=true;
        if(key==GLFW_KEY_S)backwards=true;
        if(key==GLFW_KEY_A)left=true;
        if(key==GLFW_KEY_D)right=true;
        if(key==GLFW_KEY_SPACE)up=true;
        if(key==GLFW_KEY_LEFT_SHIFT)down=true;
    }
    public void keyUp(int key){
        if(key==GLFW_KEY_W)forward=false;
        if(key==GLFW_KEY_S)backwards=false;
        if(key==GLFW_KEY_A)left=false;
        if(key==GLFW_KEY_D)right=false;
        if(key==GLFW_KEY_SPACE)up=false;
        if(key==GLFW_KEY_LEFT_SHIFT)down=false;
    }
    public void mouseDown(){
        mouseDown=true;
    }
    public void mouseUp(){
        mouseDown=false;
    }
    public void mouseMove(float x,float y){
        if(mouseDown){
            yaw+=(x-mx)/sensitivity;
            pitch+=(y-my)/sensitivity;
        }
        mx=x;
        my=y;
    }
    public void update(float delta){

        Vector2f forwardDir=new Vector2f(
            (float) (Math.cos(yaw)*speed*delta),
            (float) (Math.sin(yaw)*speed*delta)
        );
        Vector2f rightDir=new Vector2f(
            (float) (Math.cos(yaw+Math.toRadians(90))*speed*delta),
            (float) (Math.sin(yaw+Math.toRadians(90))*speed*delta)
        );

        if(forward){x+=forwardDir.x;z+=forwardDir.y;}
        if(backwards){x-=forwardDir.x;z-=forwardDir.y;}
        if(left){x-=rightDir.x;z-=rightDir.y;}
        if(right){x+=rightDir.x;z+=rightDir.y;}
        if(up){y+=speed*delta;}
        if(down){y-=speed*delta;}

        Vector3f dir=new Vector3f();
        dir.x= (float) (Math.cos(yaw)*Math.cos(pitch));
        dir.y= (float) Math.sin(pitch);
        dir.z= (float) (Math.sin(yaw)*Math.cos(pitch));

        camera.lookAt(dir);

        camera.setPosition(x,y,z);
        camera.update();
    }
}
