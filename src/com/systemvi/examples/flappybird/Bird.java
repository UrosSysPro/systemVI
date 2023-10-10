package com.systemvi.examples.flappybird;

import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Bird {
    public float x,y,width,height,speed,acceleration;
    private boolean collision=false;
    public Vector4f color;
    public Bird(Window window){
        color=new Vector4f(1,0,0,1);
        width=50;
        height=50;
        x=100;
        y=window.getHeight()/2-height/2;
        speed=0;
        acceleration=600;
    }

    public void update(float delta,Window window,Wall[] walls){
        //move
        speed+=acceleration*delta;
        y+=speed*delta;
        if(y+height>window.getHeight()){
            y=window.getHeight()-height;
            speed=0;
        }
        //collision
        for(int i=0;i<walls.length;i++){
            if(!(x>walls[i].x+walls[i].width||x+width<walls[i].x)){
                if(!(y>walls[i].y&&y<walls[i].y+walls[i].opening)){
                    collision=true;
                }
            }
        }
    }

    public boolean hasCollided(){
        return collision;
    }

    public void draw(ShapeRenderer renderer){
        renderer.rect(x,y,width,height,color);
    }
}
