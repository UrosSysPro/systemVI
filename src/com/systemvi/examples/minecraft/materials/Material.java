package com.systemvi.examples.minecraft.materials;


import com.systemvi.engine.texture.Texture;

import static org.lwjgl.opengl.GL33.*;
public class Material {
    public final Texture diffuse,specular,normal;
    public Material(){
        diffuse=new Texture("assets/examples/test3d/rock/diffuse.png");
        diffuse.setSamplerFilter(GL_LINEAR_MIPMAP_NEAREST,GL_NEAREST);
        specular=new Texture("assets/examples/test3d/rock/roughness.png");
        specular.setSamplerFilter(GL_LINEAR_MIPMAP_NEAREST,GL_NEAREST);
        normal=new Texture("assets/examples/test3d/rock/normal.png");
        normal.setSamplerFilter(GL_LINEAR_MIPMAP_NEAREST,GL_NEAREST);
    }
    public Material(String path){
        diffuse=new Texture(path+"/diffuse.png");
        diffuse.setSamplerFilter(GL_LINEAR_MIPMAP_NEAREST,GL_NEAREST);
        specular=new Texture(path+"/roughness.png");
        specular.setSamplerFilter(GL_LINEAR_MIPMAP_NEAREST,GL_NEAREST);
        normal=new Texture(path+"/normal.png");
        normal.setSamplerFilter(GL_LINEAR_MIPMAP_NEAREST,GL_NEAREST);
    }
}
