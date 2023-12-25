package com.systemvi.engine.model;

import com.systemvi.engine.shader.Shader;

import static org.lwjgl.opengl.GL33.*;

public class Mesh {
    private int vertexBuffer=0,arrayBuffer=0,elementBuffer=0,instanceBuffer=0;
    public VertexAttribute[] attributes;
    private float[] vertices=null,instanceData=null;
    private int[] indices=null;
    private boolean elementsEnabled=false,instancingEnabled=false;

    public Mesh(VertexAttribute... attributes){
        this.attributes=attributes;
        arrayBuffer=glGenVertexArrays();
        vertexBuffer=glGenBuffers();
        glBindVertexArray(arrayBuffer);
        glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);


        int vertexSize=0;
        for (VertexAttribute attribute : attributes) vertexSize += attribute.size;

        long pointer=0;
        int sizeOfFloat=4;
        for(int i=0;i<attributes.length;i++){
            glVertexAttribPointer(i, attributes[i].size, GL_FLOAT, false, vertexSize*sizeOfFloat , pointer*sizeOfFloat);
            glEnableVertexAttribArray(i);
            pointer+=attributes[i].size;
        }
        glBindVertexArray(0);
    }

    public void setVertexData(float[] vertices){
        this.vertices=vertices;
        glBindVertexArray(arrayBuffer);
        glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);
        glBindVertexArray(0);
    }
    public void setIndices(int[] indices){
        this.indices=indices;
        glBindVertexArray(arrayBuffer);
        if(!elementsEnabled){
            elementsEnabled=true;
            elementBuffer=glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,elementBuffer);
        }
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices,GL_STATIC_DRAW);
        glBindVertexArray(0);
    }
    public void enableInstancing(VertexAttribute... instanceAttributes){
        instancingEnabled=true;
        glBindVertexArray(arrayBuffer);
        instanceBuffer=glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,instanceBuffer);

        int instanceSize=0;
        for(VertexAttribute v:instanceAttributes)instanceSize+=v.size;
        long pointer=0;
        int attributeIndexStart=attributes.length;

        for(int i=0;i<instanceAttributes.length;i++){
            glVertexAttribPointer(attributeIndexStart+i,instanceAttributes[i].size,GL_FLOAT,false,instanceSize*4,pointer*4);
            glEnableVertexAttribArray(attributeIndexStart+i);
            pointer+=instanceAttributes[i].size;
        }
        for(int i=0;i<instanceAttributes.length;i++){
            glVertexAttribDivisor(attributeIndexStart+i,1);
        }
        glBindVertexArray(0);
    }
    public void setInstanceData(float[] instanceData){
        this.instanceData=instanceData;
        glBindVertexArray(arrayBuffer);
        glBindBuffer(GL_ARRAY_BUFFER,instanceBuffer);
        glBufferData(GL_ARRAY_BUFFER,instanceData,GL_STATIC_DRAW);
        glBindVertexArray(0);
    }
    public void draw(){
        glBindVertexArray(arrayBuffer);
        glDrawArrays(GL_TRIANGLES,0,vertices.length);
        glBindVertexArray(0);
    }
    public void draw(int count){
        glBindVertexArray(arrayBuffer);
        glDrawArrays(GL_TRIANGLES,0,count);
        glBindVertexArray(0);
    }
    public void draw(int offset,int count){
        glBindVertexArray(arrayBuffer);
        glDrawArrays(GL_TRIANGLES,offset,count);
        glBindVertexArray(0);
    }
    public void drawElements(int trianglesToDraw){
        glBindVertexArray(arrayBuffer);
        glDrawElements(GL_TRIANGLES,trianglesToDraw*3,GL_UNSIGNED_INT,0);
        glBindVertexArray(0);
    }
    public void drawInstancedElements(int trianglesToDraw,int instancesToDraw){
        glBindVertexArray(arrayBuffer);
        glDrawElementsInstanced(GL_TRIANGLES,trianglesToDraw*3,GL_UNSIGNED_INT,0,instancesToDraw);
        glBindVertexArray(0);
    }
    public void drawInstanced(int instancesToDraw){
        glBindVertexArray(arrayBuffer);
        glDrawArraysInstanced(GL_TRIANGLES,0,vertices.length,instancesToDraw);
        glBindVertexArray(0);
    }
    public void drawInstanced(int verticesToDraw,int instancesToDraw){
        glBindVertexArray(arrayBuffer);
        glDrawArraysInstanced(GL_TRIANGLES,0,verticesToDraw,instancesToDraw);
        glBindVertexArray(0);
    }

    public void delete(){
        glBindVertexArray(arrayBuffer);
        //brisanje vertex buffera
        glBindBuffer(GL_ARRAY_BUFFER,0);
        glDeleteBuffers(vertexBuffer);
        //brisanje elements buffera
        if(elementsEnabled){
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
            glDeleteBuffers(elementBuffer);
        }
        //brisanje instance buffera
        if(instancingEnabled){
            glBindBuffer(GL_ARRAY_BUFFER,0);
            glDeleteBuffers(instanceBuffer);
        }

        glDeleteVertexArrays(arrayBuffer);
    }
}
