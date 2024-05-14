package com.systemvi.engine.buffer;

import static org.lwjgl.opengl.GL33.*;

public class ElementsBuffer {
    private int id;
    public ElementsBuffer() {
        id=glGenBuffers();
    }
    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }
    public void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    public int id(){
        return id;
    }
    public void setData(int[] data){
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
    }
    public void setData(short[] data){
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
    }
    public void setSubData(int offset,int[] data){
        bind();
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, offset, data);
    }
    public void setSubData(int offset,short[] data){
        bind();
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, offset, data);
    }
    public void delete(){
        glDeleteBuffers(id);
    }
}
