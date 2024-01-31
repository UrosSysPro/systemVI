package com.systemvi.engine.shader;
import static org.lwjgl.opengl.GL33.*;
public enum ElementsDataType {
    UNSIGNED_INT(GL_UNSIGNED_INT),
    UNSIGNED_SHORT(GL_UNSIGNED_SHORT),
    UNSIGNED_BYTE(GL_UNSIGNED_BYTE)
    ;

    public final int id;
    ElementsDataType(int id){
        this.id=id;
    }
}
