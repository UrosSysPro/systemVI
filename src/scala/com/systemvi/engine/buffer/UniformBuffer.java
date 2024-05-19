package com.systemvi.engine.buffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
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

    public void setSubData(int byteOffset,int[] data){
        bind();
        glBufferSubData(GL_UNIFORM_BUFFER,byteOffset,data);
        unbind();
    }
    public void setSubData(int byteOffset,short[] data){
        bind();
        glBufferSubData(GL_UNIFORM_BUFFER,byteOffset,data);
        unbind();
    }
    public void setSubData(int byteOffset,float[] data){
        bind();
        glBufferSubData(GL_UNIFORM_BUFFER,byteOffset,data);
        unbind();
    }
    public void setSubData(int byteOffset, Vector4f[] data){
        bind();
        float[] buffer = new float[data.length*4];
        for(int i=0;i<data.length;i++){
            buffer[i*4] = data[i].x;
            buffer[i*4+1] = data[i].y;
            buffer[i*4+2] = data[i].z;
            buffer[i*4+3] = data[i].w;
        }
        glBufferSubData(GL_UNIFORM_BUFFER,byteOffset,buffer);
        unbind();
    }
    public void setSubData(int byteOffset, Vector3f[] data){
        bind();
        float[] buffer = new float[data.length*3];
        for(int i=0;i<data.length;i++){
            buffer[i*4] = data[i].x;
            buffer[i*4+1] = data[i].y;
            buffer[i*4+2] = data[i].z;
        }
        glBufferSubData(GL_UNIFORM_BUFFER,byteOffset,buffer);
        unbind();
    }
    public void setSubData(int byteOffset, Matrix4f[] data){
        bind();
        float[] buffer = new float[data.length*16];
        for(int i=0;i<data.length;i++){
            for(int j=0;j<16;j++){
                float[] f=new float[16];
                data[i].get(f);
                buffer[i*16+j] = f[j];
            }
        }
        glBufferSubData(GL_UNIFORM_BUFFER,byteOffset,buffer);
        unbind();
    }

    public void bind(){
        glBindBuffer(GL_UNIFORM_BUFFER,id);
    }
    public void unbind(){
        glBindBuffer(GL_UNIFORM_BUFFER,0);
    }
}
