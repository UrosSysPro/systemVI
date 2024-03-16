package com.systemvi.examples.noise;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Random;

public class SimpleNoise {
    public static float getCheckerboard(float u, float v){
        if ((2*u + 2*v) % 2.0f < 0.5f)
            return 0.0f;
        else
            return 1.0f;
    }

    public static float getStripes(float x, float y){
        Vector4f vector = new Vector4f(x, 0f, 0f, 1f);
        Matrix4f rotation = new Matrix4f().rotateZ(y);
        vector.mul(rotation);
        return (float)(Math.sin(vector.x));
    }

    public static float getRipples(float u, float v){
        return (float)Math.sin((u-0.5f)*(u-0.5f) + (v-0.5f)*(v-0.5f));
    }

    public static float getWhiteNoise(){
        Random r = new Random();
        return r.nextFloat();
    }
}
