package com.systemvi.examples.sdf;

import org.joml.Vector4f;

public class Material {
    float roughness;
    float metallic;
    Vector4f color;

    public Material(float r, float m, Vector4f c){
        roughness = r;
        metallic = m;
        color = c;
    }

    public float getMetallic() {
        return metallic;
    }

    public float getRoughness() {
        return roughness;
    }

    public Vector4f getColor() {
        return color;
    }
}
