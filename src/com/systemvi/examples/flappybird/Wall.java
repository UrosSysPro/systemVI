package com.systemvi.examples.flappybird;

import com.systemvi.engine.renderers.ShapeRenderer;
import org.joml.Vector4f;

import java.awt.*;

public class Wall {
    public float opening,width,x,y,speed;
    public Vector4f color;
    public Wall(){
        color=new Vector4f(0,1,0,1);
        opening=300;
        width=50;
        x=700;
        y=200;
        speed=100;
    }
    public void update(float delta){
        x-=speed*delta;
        if(x<-width){
            x=800;
            y=(float)Math.random()*300;
        }
    }
    public void draw(ShapeRenderer renderer){
        renderer.rect(x,y-800,width,800,color);
        renderer.rect(x,y+opening,width,800,color);
    }
}
