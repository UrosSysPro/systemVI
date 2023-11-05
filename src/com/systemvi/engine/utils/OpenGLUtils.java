package com.systemvi.engine.utils;

import org.joml.Vector4f;

import static org.lwjgl.opengl.GL33.*;
public class OpenGLUtils {
    public static void clearColor(float r,float g,float b,float a){
        glClearColor(r,g,b,a);
        glClear(GL_COLOR_BUFFER_BIT);
    }
    public static void clearColor(Vector4f color){
        glClearColor(color.x,color.y,color.z,color.w);
        glClear(GL_COLOR_BUFFER_BIT);
    }
    public static void clearColorAndDepth(float r,float g,float b,float a){
        glClearColor(r,g,b,a);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
    }
    public static void clearColorAndDepth(Vector4f color){
        glClearColor(color.x,color.y,color.z,color.w);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
    }
    public static void enableBlending(){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
    }
    public static void disableBlending(){
        glDisable(GL_BLEND);
    }
}
