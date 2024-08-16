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
        glBufferSubData(GL_ARRAY_BUFFER,offset* 4L,data);
    }

    public void delete(){
        glDeleteBuffers(id);
    }

    public void setVertexAttributes(int offset,VertexAttribute[] attributes){
        bind();
        int vertexSize=0;
        for (VertexAttribute attribute : attributes) vertexSize += attribute.size;

        long pointer=0;
        int sizeOfFloat=4;
        for(int i=0;i<attributes.length;i++){
            glVertexAttribPointer(offset+i, attributes[i].size, GL_FLOAT, false, vertexSize*sizeOfFloat , pointer*sizeOfFloat);
            glEnableVertexAttribArray(i);
            pointer+=attributes[i].size;
        }
    }

    public void setVertexAttributes(VertexAttribute[] attributes){
        setVertexAttributes(0,attributes);
    }

    public void enableVertexAttribute(int index){
        glEnableVertexAttribArray(index);
    }

    public void disableVertexAttribute(int index){
        glDisableVertexAttribArray(index);
    }

    public void setVertexAttributeDivisor(int index,int divisor){
        bind();
        glVertexAttribDivisor(index,divisor);
    }
}
