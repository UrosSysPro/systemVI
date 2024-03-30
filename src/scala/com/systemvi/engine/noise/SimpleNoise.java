package com.systemvi.engine.noise;

public class SimpleNoise {
//    public static float getCheckerboard(int x, int y, int width) {
//        if ((x / width) % 2 == (y / width) % 2)
//            return 1f;
//        else
//            return 0f;
//    }
//
//    public static float getStripes(float x, float y, float rotation, float s_width) {
//        Vector4f vector = new Vector4f(x, y, 0, 1);
//        Matrix4f matrix = new Matrix4f().rotationZ(rotation);
//        vector.mul(matrix);
//        if (vector.x < 0)
//            vector.x -= 30;
//        vector.x = Math.abs(vector.x);
//        return (int) (vector.x / s_width) % 2;
//    }
//
//    public static float getRipples(float u, float v) {
//        return (float) Math.sin(Math.sqrt((u - 0.5f) * (u - 0.5f) + (v - 0.5f) * (v - 0.5f)));
//    }
//
//    public static float getWhiteNoise(int seed) {
//        Random r = new Random();
//        return r.nextFloat();
//    }
}