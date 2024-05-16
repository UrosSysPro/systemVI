package com.systemvi.engine.buffer;

import org.joml.Vector4f;

import static org.lwjgl.opengl.GL33.*;

public class UniformBuffer {
    private int id;
    public int id(){
        return id;
    }
    public UniformBuffer(int byteCapacity){
        id=glGenBuffers();
        bind();
        glBufferData(GL_UNIFORM_BUFFER, byteCapacity, GL_STATIC_DRAW);
        unbind();
    }

    public void setSubData(int offset,int[] data){
        bind();
        glBufferSubData(GL_UNIFORM_BUFFER,offset,data);
        unbind();
    }
    public void setSubData(int offset,short[] data){
        bind();
        glBufferSubData(GL_UNIFORM_BUFFER,offset,data);
        unbind();
    }
    public void setSubData(int offset,float[] data){
        bind();
        glBufferSubData(GL_UNIFORM_BUFFER,offset,data);
        unbind();
    }
    public void setSubData(int offset, Vector4f[] data){
        bind();
//        glBufferSubData(GL_UNIFORM_BUFFER,offset,data);
        unbind();
    }

    public void bind(){
        glBindBuffer(GL_UNIFORM_BUFFER,id);
    }
    public void unbind(){
        glBindBuffer(GL_UNIFORM_BUFFER,0);
    }
}
