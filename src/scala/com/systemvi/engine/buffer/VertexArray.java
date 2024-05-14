package com.systemvi.engine.buffer;

import static org.lwjgl.opengl.GL33.*;

public class VertexArray {
    private int id;

    public VertexArray() {
        id=glGenVertexArrays();
    }
    public int id(){
        return id;
    }
    public void bind() {
        glBindVertexArray(id);
    }
    public void unbind() {
        glBindVertexArray(0);
    }
    public void destroy() {
        glDeleteVertexArrays(id);
    }
}
