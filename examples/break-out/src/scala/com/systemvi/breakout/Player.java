package com.systemvi.breakout;

import com.systemvi.engine.renderers.ShapeRenderer;
import org.joml.Vector4f;

public class Player {
    public int x,y,width,height;
    public Vector4f color;
    public Player(int screenWidth,int screenHeight){
        width=100;
        height=30;
        x=(screenWidth-width)/2;
        y=screenHeight-100;
        color=new Vector4f(0.5f,0.5f,0.5f,1.0f);
    }
    public void draw(ShapeRenderer renderer){
        renderer.rect(x,y,width,height,color);
    }
}
