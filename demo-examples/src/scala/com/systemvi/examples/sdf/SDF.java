package com.systemvi.examples.sdf;

import org.joml.Vector3f;

public class SDF {
    public static float Sphere(Vector3f p, float r){
        return p.length() - r;
    }

    public static float Plain(Vector3f p){
        return p.y;
    }

    public static Vector3f Translate(Vector3f p, Vector3f translate){
        return new Vector3f(p).sub(translate);
    }

    public static float Union(float d1, float d2){
        return Math.min(d1, d2);
    }

    public  static float Union(float... oblici){
        float d = Float.MAX_VALUE;
        for (float f :
                oblici) {
            d = Math.min(d, f);
        }
        return d;
    }
}
