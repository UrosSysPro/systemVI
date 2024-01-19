package com.systemvi.engine.texture;

import static org.lwjgl.opengl.GL33.*;

public enum Format {
    RGBA(GL_RGBA,4),
    RGB(GL_RGB,3),
    RG(GL_RG,2),
    R(GL_R,1),
    RGBA32(GL_RGBA32F,4),
    RGB32(GL_RGB32F,3),
    RG32(GL_RG32F,2),
    R32(GL_R32F,1),
    RGBA16(GL_RGBA16,4),
    RGB16(GL_RGB16,3),
    RG16(GL_RG16,2),
    R16(GL_R16,1),
    RGBA8(GL_RGBA8,4),
    RGB8(GL_RGB8,3),
    RG8(GL_RG8,2),
    R8(GL_R8,1),
    DEPTH(GL_DEPTH_COMPONENT,1),
    DEPTH16(GL_DEPTH_COMPONENT16,1),
    DEPTH32(GL_DEPTH_COMPONENT32,1),
    DEPTH24(GL_DEPTH_COMPONENT24,1),
    ;
    public final int id,channels;

    Format(int id,int channels){
        this.id=id;
        this.channels=channels;
    }
}
