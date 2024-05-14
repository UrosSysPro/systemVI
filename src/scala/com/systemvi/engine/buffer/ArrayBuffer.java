package com.systemvi.engine.buffer;

import com.systemvi.engine.model.VertexAttribute;

import static org.lwjgl.opengl.GL33.*;

public class ArrayBuffer {
    private int id;
    public ArrayBuffer() {
        id=glGenBuffers();
    }
    public int id(){
        return id;
    }
    public void bind(){
        glBindBuffer(GL_ARRAY_BUFFER,id);
    }
    public void unbind(){
        glBindBuffer(GL_ARRAY_BUFFER,0);
    }
    public void setData(float[] data){
        bind();
        glBufferData(GL_ARRAY_BUFFER,data,GL_STATIC_DRAW);
    }
    public void setSubData(int offset,float[] data){
        bind();
        glBufferSubData(GL_ARRAY_BUFFER,offset,data);
    }
    public void delete(){
        glDeleteBuffers(id);
    }

    public void vertexAttribute(int index, VertexAttribute[] attributes){

    }
    public void enableVertexAttribute(int index){
        glEnableVertexAttribArray(index);
    }
    public void disableVertexAttribute(int index){
        glDisableVertexAttribArray(index);
    }
    public void vertexAttributeDivisor(int index,int divisor){

    }
}
