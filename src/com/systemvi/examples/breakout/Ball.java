package com.systemvi.examples.breakout;

import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.window.Window;
import org.joml.Vector4f;

public class Ball {
    public float x,y,vx,vy,r;
    public Vector4f color;
    public Ball(int screenWidth,int screenHeight){
        color=new Vector4f(1,1,1,1);
        x=screenWidth/2;
        y=screenHeight/2;
        vx=200;
        vy=-200;
        r=15;
    }
    public void update(float delta, Wall[][] walls, Window window,Player player,ParticleSimulation particleSimulation){
        float[] putX=new float[]{vx*delta,0};
        float[] putY=new float[]{0,vy*delta};

        for(int l=0;l<2;l++){
            //kretanje
            x+=putX[l];
            y+=putY[l];
            // sudaranje sa ekranom
            for(int i=0;i<walls.length;i++){
                for(int j=0;j<walls[i].length;j++){
                    if(walls[i][j].visible && !(
                        walls[i][j].y>y+r||
                            walls[i][j].x>x+r||
                            walls[i][j].x+Wall.width<x-r||
                            walls[i][j].y+Wall.height<y-r
                    )){
                        //dodati particle
                        particleSimulation.addParticles(x,y,5);
                        if(putX[l]!=0)vx=-vx;
                        if(putY[l]!=0)vy=-vy;
                        walls[i][j].visible=false;
                    }
                }
            }
        }
        if(x-r<0)vx*=-1;
        if(y-r<0)vy*=-1;
        if(x+r>window.getWidth())vx*=-1;
        if(y+r>window.getHeight())vy*=-1;
        //sudaranje sa igracem
        if(
            player.y>y+r||
                player.x>x+r||
                player.x+player.width<x-r||
                player.y+player.height<y-r
        ){
            //ne sudaraju se
        }else{
            //sudaraju se
            //dodoti particle
            particleSimulation.addParticles(x,y,5);
            vy=-vy;
        }
    }
    public void draw(ShapeRenderer renderer){
        renderer.rect(x-r,y-r,2*r,2*r,color);
    }
}
