package com.systemvi.examples.inversekinematics;

public class Vector {
    public float x,y;
    public Vector(float x,float y){
        this.x=x;
        this.y=y;
    }
    public Vector add(Vector v){
        x+=v.x;
        y+=v.y;
        return this;
    }
    public float intensity(){
        return (float)Math.sqrt(x*x+y*y);
    }
    public Vector normalize(){
        multiply(1f/intensity());
        return this;
    }
    public Vector multiply(float a){
        x*=a;
        y*=a;
        return this;
    }
    public Vector sub(Vector v){
        x-=v.x;
        y-=v.y;
        return this;
    }
}
