package com.systemvi.engine.shader;
import static org.lwjgl.opengl.GL33.*;
public enum Primitive {

    TRIANGLES(GL_TRIANGLES),
    LINES(GL_LINES),
    POINTS(GL_POINTS),
    TIRANGLE_STRIP(GL_TRIANGLE_STRIP),
    LINE_STRIP(GL_LINE_STRIP),
    ;
    public final int id;

    Primitive(int id){
        this.id=id;
    }
}
