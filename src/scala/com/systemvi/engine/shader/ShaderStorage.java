package com.systemvi.engine.shader;
import static org.lwjgl.opengl.GL46.*;
public class ShaderStorage {
    private final int id;

    public ShaderStorage(float[] data){
        id=glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id);
        glBufferData(GL_SHADER_STORAGE_BUFFER, data,GL_STATIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }
    public void bind(int index){
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, index, id);
    }

    public void setData(float[] data){
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id);
        glBufferData(GL_SHADER_STORAGE_BUFFER, data,GL_STATIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }
    public void setData(float[] data,int offset){
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, id);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER,offset,data);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }
}
