package com.systemvi.engine.model;

import static org.lwjgl.opengl.GL33.*;

public class Mesh {
    private int vertexBuffer,arrayBuffer,elementBuffer,instanceBuffer;
    public VertexAttribute[] attributes;

    public Mesh(VertexAttribute... attributes){
        this.attributes=attributes;
        arrayBuffer=glGenVertexArrays();
        vertexBuffer=glGenBuffers();
        glBindVertexArray(arrayBuffer);
        glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
    }

    public void setVertexData(float[] vertices){
        glBindVertexArray(arrayBuffer);
        glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);
        long pointer=0;
        for(int i=0;i<attributes.length;i++){
            glVertexAttribPointer(i, attributes[i].size, GL_FLOAT, false, attributes[i].size*4 , pointer);
            pointer+=attributes[i].size;
        }
        for(int i=0;i<attributes.length;i++){
            glEnableVertexAttribArray(i);
        }
    }

}
