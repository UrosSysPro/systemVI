package com.systemvi.examples.flappybird;

import com.systemvi.engine.renderers.ShapeRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Bird {
    public float x,y,width,height;
    public Vector4f color;
    public Bird(){
        color=new Vector4f(1,0,0,1);
        x=100;
        y=300;
        width=50;
        height=50;
    }

    public void draw(ShapeRenderer renderer){
        renderer.rect(x,y,width,height,color);
    }
}
