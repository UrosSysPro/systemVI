package com.systemvi.examples.breakout;

import java.util.Random;

public class Particle {
    private static Random random=new Random();

    public float x,y,vx,vy,size,rotation,lifespan;
    public Particle(float x,float y){
        this.x=x;
        this.y=y;
        vx=random.nextFloat()*100-50;
        vy= random.nextFloat()*100-50;
        size=random.nextInt()%8+2;
        lifespan=1;
        rotation= random.nextFloat()*(float) Math.PI*2;
    }
}
