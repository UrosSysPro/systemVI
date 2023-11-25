package com.systemvi.engine.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL33.*;
public class OpenGLUtils {
   public enum Buffer{
       COLOR_BUFFER(GL_COLOR_BUFFER_BIT),
       DEPTH_BUFFER(GL_DEPTH_BUFFER_BIT),
       STENCIL_BUFFER(GL_STENCIL_BUFFER_BIT);
       public final int value;
       private Buffer(int value){
           this.value=value;
       }
   }
   public enum Face{
       FRONT(GL_FRONT),
       BACK(GL_BACK);
       public final int value;
       private Face(int value){
           this.value=value;
       }
   }
   public static void clear(float r,float g,float b,float a,Buffer ...buffers){
       glClearColor(r,g,b,a);
       int mask=0;
       for(Buffer buffer : buffers){
           mask|=buffer.value;
       }
       glClear(mask);
   }
   public static void clearDepth(){
       glClear(GL_DEPTH_BUFFER_BIT);
   }
   public static void clear(Vector4f color,Buffer ...buffers){
       clear(color.x,color.y,color.z,color.w,buffers);
   }
    public static void clear(Vector3f color, Buffer ...buffers){
        clear(color.x,color.y,color.z,1,buffers);
    }
    public static void enableBlending(){
       glEnable(GL_BLEND);
       glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
    }
    public static void disableBlending(){
       glDisable(GL_BLEND);
    }
    public static void enableFaceCulling(Face face){
       glEnable(GL_CULL_FACE);
       glCullFace(face.value);
    }
    public static void enableFaceCulling(){
        enableFaceCulling(Face.FRONT);
    }
    public static void disableFaceCulling(){
       glDisable(GL_CULL_FACE);
    }

    public static void enableDepthTest(){
       glEnable(GL_DEPTH_TEST);
    }
    public static void disableDepthTest(){
       glDisable(GL_DEPTH_TEST);
    }

}
