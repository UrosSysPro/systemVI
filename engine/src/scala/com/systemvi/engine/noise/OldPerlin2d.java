package com.systemvi.engine.noise;

import org.joml.Vector2f;

import java.util.Random;

public class OldPerlin2d {
    private Vector2f[][] grid;
    public OldPerlin2d(int seed, int width, int height){
        Random random=new Random(seed);
        grid=new Vector2f[width][height];
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                int direction=random.nextInt(4);
                grid[i][j]=new Vector2f(
                    direction%2==0?-1:1,
                    direction/2==0?-1:1
                );
            }
        }
    }
    public float get(float x,float y){
        int i=(int)x;
        int j=(int)y;

        float dx=x-i;
        float dy=y-j;

        Vector2f topLeft=grid[i][j];
        Vector2f topRight=grid[i+1][j];
        Vector2f bottomLeft=grid[i][j+1];
        Vector2f bottomRight=grid[i+1][j+1];

        Vector2f point=new Vector2f();
        float dotTopLeft=topLeft.dot(point.set(dx,dy));
        float dotTopRight=topRight.dot(point.set(dx-1,dy));
        float dotBottomLeft=bottomLeft.dot(point.set(dx,dy-1));
        float dotBottomRight=bottomRight.dot(point.set(dx-1,dy-1));

        float top=smooth(1-dx)*dotTopLeft+smooth(dx)*dotTopRight;
        float bottom=smooth(1-dx)*dotBottomLeft+smooth(dx)*dotBottomRight;

        float value=top*smooth(1-dy)+smooth(dy)*bottom;

        value=(value+1)/2;

        return (value);
    }

    private float smooth(float a){
        return 6*a*a*a*a*a - 15*a*a*a*a + 10*a*a*a;
    }
}
