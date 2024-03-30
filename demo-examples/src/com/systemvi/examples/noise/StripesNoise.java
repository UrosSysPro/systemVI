package com.systemvi.examples.noise;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class StripesNoise extends NoiseFunction{

    private final float angle;
    private final float width;
    public StripesNoise(float angle, float width){
        this.angle = (float) Math.toRadians(angle);
        this.width = width;
    }
    @Override
    public float get(float x, float y) {
        Vector4f vector = new Vector4f(x, y, 0, 1);
        Matrix4f matrix = new Matrix4f().rotationZ(angle);
        vector.mul(matrix);
        if (vector.x < 0)
            vector.x -= 30;
        vector.x = Math.abs(vector.x);
        return (int) (vector.x / width) % 2;
    }
}
