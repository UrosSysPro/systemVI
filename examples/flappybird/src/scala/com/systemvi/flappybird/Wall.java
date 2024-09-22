package com.systemvi.flappybird;

import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.window.Window;
import org.joml.Vector4f;


public class Wall {
    public final float worldWidth=2000;
    public float opening,width,x,y,speed,height=800;
    public Vector4f color;
    public Wall(Window window,int i){
        color=new Vector4f(0,1,0,1);
        opening=300;
        width=50;
        x=window.getWidth()+i*400;
        y= (float) (Math.random()*(window.getHeight()-opening));
        speed=100;
    }
    public void update(float delta,Window window){
        x-=speed*delta;
        if(x<-width){
            x= worldWidth;
            y=(float)Math.random()*(window.getHeight()-opening);
        }
    }
    public void draw(ShapeRenderer renderer){
        renderer.rect(x,y-height,width,height,color);
        renderer.rect(x,y+opening,width,height,color);
    }
}
