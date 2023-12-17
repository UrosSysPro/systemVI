package com.systemvi.engine.noise;

import org.joml.Vector2f;

import java.util.Random;

public class Perlin2d {
    private Vector2f[][] grid;
    public Perlin2d(int seed,int width,int height){
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
    public float get(int x,int y){

        return 0.5f;
    }
}
