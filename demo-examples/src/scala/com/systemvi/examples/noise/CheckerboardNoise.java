package com.systemvi.examples.noise;

public class CheckerboardNoise extends NoiseFunction{
    private final int width;

    public CheckerboardNoise(int width){
        this.width = width;
    }

    @Override
    public float get(float x, float y) {
        int u = (int) x;
        int v = (int) y;
        if ((u / width) % 2 == (v / width) % 2)
            return 1f;
        else
            return 0f;
    }
}
