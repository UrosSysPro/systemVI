package com.systemvi.engine.noise;

import java.util.Random;

public class WhiteNoise extends NoiseFunction {
    private final int seed;
    private final int width;
    private final int height;
    private Random r;

    public WhiteNoise(int seed, int width, int height){
        this.seed = seed;
        this.width = width;
        this.height = height;
        r = new Random(seed);
    }
    @Override
    public float get(float x, float y) {
        if (x >= width && y >= height)
            r = new Random(seed);
        return r.nextFloat();
    }
}
