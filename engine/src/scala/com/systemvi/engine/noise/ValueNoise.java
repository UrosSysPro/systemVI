package com.systemvi.engine.noise;

import java.util.Random;

public class ValueNoise extends NoiseFunction {
    private final int size;
    private final int[] permutacije;
    private final float[] values;
    public ValueNoise(int seed, int size){
        this.size = size;
        permutacije = new int[size * 2];
        values = new float[size];
        Random r = new Random(seed);

        for (int i = 0; i < size; i++) {
            values[i] = r.nextFloat();
            permutacije[i] = i;
        }
        for (int i = 0; i < size; i++) {
            int j = r.nextInt(size);
            int swap = permutacije[i];
            permutacije[i] = permutacije[j];
            permutacije[j] = swap;
            permutacije[i + size] = permutacije[i];
        }
    }
    @Override
    public float get(float x, float y) {
        int xi = (int) Math.floor(x) & (size - 1);
        int yi = (int) Math.floor(y) & (size - 1);
        float xf = x - (float) Math.floor(x);
        float yf = y - (float) Math.floor(y);
        float u = fade(xf);
        float v = fade(yf);
        int aa = permutacije[permutacije[xi] + yi];
        int ab = permutacije[permutacije[xi] + yi + 1];
        int ba = permutacije[permutacije[xi + 1] + yi];
        int bb = permutacije[permutacije[xi + 1] + yi + 1];
        float x1, x2, y1;
        x1 = lerp(values[aa], values[ba], u);
        x2 = lerp(values[ab], values[bb], u);
        y1 = lerp(x1, x2, v);
        return y1;
    }

    private float lerp(float a, float b, float x){
        return a + x * (b - a);
    }

    private float fade(float t){
        return t * t * t * (t * (t * 6 - 15) + 10);
    }
}
