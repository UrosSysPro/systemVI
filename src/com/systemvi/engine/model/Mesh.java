package com.systemvi.engine.model;

import com.systemvi.engine.shader.Shader;

import static org.lwjgl.opengl.GL33.*;

public class Mesh {
    private int vertexBuffer=0,arrayBuffer=0,elementBuffer=0,instanceBuffer=0;
    public VertexAttribute[] attributes;
    private float[] vertices=null,indices=null,instanceData=null;

    public Mesh(VertexAttribute... attributes){
        this.attributes=attributes;
        arrayBuffer=glGenVertexArrays();
        vertexBuffer=glGenBuffers();
        glBindVertexArray(arrayBuffer);
        glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
    }

    public void setVertexData(float[] vertices){
        this.vertices=vertices;

        glBindVertexArray(arrayBuffer);
        glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);

        int vertexSize=0;
        for (VertexAttribute attribute : attributes) vertexSize += attribute.size;

        long pointer=0;
        for(int i=0;i<attributes.length;i++){
            glVertexAttribPointer(i, attributes[i].size, GL_FLOAT, false, vertexSize*4 , pointer*4);
//            glEnableVertexAttribArray(i);
            pointer+=attributes[i].size;
        }
        for(int i=0;i<attributes.length;i++){
            glEnableVertexAttribArray(i);
        }
    }
    public void draw(){
        glBindVertexArray(arrayBuffer);
        glDrawArrays(GL_TRIANGLES,0,vertices.length);
    }

    public void delete(){
        glDeleteBuffers(arrayBuffer);
        if(vertices!=null)glDeleteBuffers(vertexBuffer);
        if(indices!=null)glDeleteBuffers(elementBuffer);
        if(instanceData!=null)glDeleteBuffers(instanceBuffer);
    }
}
