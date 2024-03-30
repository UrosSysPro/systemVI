package com.systemvi.engine.noise;

public abstract class NoiseFunction {
    public abstract float get(float x, float y);

    public static float Compose(float x, float y, NoiseFunction... values) {
        float sum = 0;
        for (NoiseFunction noise : values) {
            sum += noise.get(x, y);
        }
        return sum / values.length;
    }

    public static float Islands(float x, float y, NoiseFunction... values) {
        float sum = 0;
        for (NoiseFunction noise : values) {
            sum += noise.get(x, y);
        }
        sum /= values.length;
        if (sum > 0.9f){
            return 1f;
        }
        else return 0f;
    }
}
