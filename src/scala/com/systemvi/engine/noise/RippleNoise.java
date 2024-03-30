package com.systemvi.engine.noise;

import org.joml.Vector2f;

public class RippleNoise extends NoiseFunction{
    private final Vector2f center;
    private final float width;

    public RippleNoise(float x, float y, float width){
        center = new Vector2f(x, y);
        this.width = width;
    }

    @Override
    public float get(float x, float y) {
        return (float) Math.sin(Math.sqrt(((x - center.x) * (x - center.x) + (y - center.y) * (y - center.y)))/width);
    }
}
