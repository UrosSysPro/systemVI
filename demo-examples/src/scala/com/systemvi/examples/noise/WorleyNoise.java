package com.systemvi.examples.noise;

import java.util.Random;

public class WorleyNoise extends NoiseFunction{
    private final int points;
    private float grid[][];
    public WorleyNoise(int width, int height, int count, int seed){
        points = count;
        grid = new float[points][2];
        Random r = new Random(seed);
        for (int i = 0; i < points; i++) {
            grid[i][0] = r.nextFloat(0, width);
            grid[i][1] = r.nextFloat(0, height);
        }
    }
    @Override
    public float get(float x, float y) {
        float minD = Float.MAX_VALUE;
        for (int i = 0; i < points; i++) {
            float dx = x - grid[i][0];
            float dy = y - grid[i][1];
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            minD = Math.min(minD, dist);
        }
        return minD;
    }
}
