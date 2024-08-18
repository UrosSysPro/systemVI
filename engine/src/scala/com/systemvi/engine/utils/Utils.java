package com.systemvi.engine.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL46.*;
public class Utils {
    public enum Buffer{
        COLOR_BUFFER(GL_COLOR_BUFFER_BIT),
        DEPTH_BUFFER(GL_DEPTH_BUFFER_BIT),
        STENCIL_BUFFER(GL_STENCIL_BUFFER_BIT);
        public final int value;
        Buffer(int value){
            this.value=value;
        }
    }
    public enum Face{
        FRONT(GL_FRONT),
        BACK(GL_BACK);
        public final int value;
        Face(int value){
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

    public static void setNumberOfPatchVertices(int n){
        glPatchParameteri(GL_PATCH_VERTICES, n);
    }

    public static void copyDepthBuffer(int sourceFrabebufferId,int destFramebufferId){

    }

    public static void enableLines(int lineWidth){
        glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
        glLineWidth(lineWidth);
    }
    public static void disableLines(){
        glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
    }

    public enum Barrier{
        IMAGE_ACCESS(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT),
        SHADER_STORAGE(GL_SHADER_STORAGE_BARRIER_BIT),
        ;
        public final int value;
        Barrier(int barrierBit){
            this.value=barrierBit;
        }
    }
    public static void barrier(Barrier... barriers){
        int mask=0;
        for(Barrier barrier:barriers){
            mask|=barrier.value;
        }
        glMemoryBarrier(mask);
    }
    public static String assetsFolder="";
    @Deprecated
    public static String readFile(String fileName){
//        return readExternal(fileName);
        return readInternal(fileName);
    }
    public static String readExternal(String fileName){
        File file = new File(assetsFolder+fileName);
        try(Scanner scanner = new Scanner(file)){
            StringBuilder text = new StringBuilder();
            while( scanner.hasNextLine() ) text.append(scanner.nextLine()).append("\n");
            return text.toString();
        }catch (Exception e){
            System.out.println("[ERROR] reading file "+file.getPath());
            e.printStackTrace();
            return "";
        }
    }
    public static String readInternal(String fileName){
        try{
            InputStream stream = Utils.class.getClassLoader().getResourceAsStream(fileName);
            if(stream == null){
                throw new Exception("[ERROR] readInternal: stream is null");
            }
            Scanner scanner = new Scanner(stream);
            StringBuilder text = new StringBuilder();
            while(scanner.hasNextLine())text.append(scanner.nextLine()).append("\n");
            scanner.close();
            return text.toString();
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}
