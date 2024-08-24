package com.systemvi.breakout;

import com.systemvi.engine.renderers.ShapeRenderer;
import org.joml.Vector4f;

public class Wall {
    public static int width=80,height=30;
    public int x,y;
    public Vector4f color;
    public boolean visible=true;

    public Wall(int x,int y,Vector4f color){
        this.x=x;
        this.y=y;
        this.color=color;
    }
    public void draw(ShapeRenderer renderer){
        int padding=2;
        renderer.rect(
            x+padding,
            y+padding,
            width-2*padding,
            height-2*padding,
            color
        );
    }
}
