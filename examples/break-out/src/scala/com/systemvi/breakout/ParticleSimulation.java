package com.systemvi.breakout;

import com.systemvi.engine.renderers.ShapeRenderer;
import org.joml.Vector4f;

import java.util.LinkedList;

public class ParticleSimulation {
    public float gravity=400;
    LinkedList<Particle> particles;
    public ParticleSimulation(){
        particles=new LinkedList<>();
    }
    public void update(float delta){
        for(Particle p:particles){
            p.vy+=delta*gravity;
            p.x+=delta*p.vx;
            p.y+=delta*p.vy;
            p.size-=delta;
            p.rotation+=delta;
            p.lifespan-=delta;
        }
        int i=0;
        while(i<particles.size()){
            if(particles.get(i).lifespan<0){
                particles.remove(i);
            }else{
                i++;
            }
        }
    }
    public void draw(ShapeRenderer renderer){
        Vector4f color=new Vector4f(1);
        for(Particle p:particles){
            color.w=p.lifespan;
            renderer.rect(p.x,p.y,p.size,p.size,color,p.rotation);
        }
    }
    public void addParticles(float x,float y,int n){
        for(int i=0;i<n;i++){
            particles.add(new Particle(x,y));
        }
    }
}
