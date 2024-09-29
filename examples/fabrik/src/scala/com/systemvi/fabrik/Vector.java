package com.systemvi.fabrik;

public class Vector {
    public float x,y;
    public Vector(float x,float y){
        this.x=x;
        this.y=y;
    }
    public Vector(Vector v){
        this(v.x,v.y);
    }

    public Vector set(Vector v){
        x=v.x;
        y=v.y;
        return this;
    }
    public Vector set(float x,float y){
        this.x=x;
        this.y=y;
        return this;
    }
    public float distance(float x,float y){
        return (float) Math.sqrt((this.x-x)*(this.x-x)+(this.y-y)*(this.y-y));
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
