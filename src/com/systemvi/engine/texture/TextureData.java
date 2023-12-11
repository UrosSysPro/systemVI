package com.systemvi.engine.texture;

import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.system.MemoryStack;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class TextureData {
    private ByteBuffer buffer;
    private int width,height;
    private Format format;
    private Vector4f helperVectorFloat;
    private Vector4i helperVectorInt;

    public TextureData(int width,int height,Format format){
        this.width=width;
        this.height=height;
        this.format=format;
        helperVectorFloat=new Vector4f();
        helperVectorInt=new Vector4i();
        buffer= ByteBuffer.allocate(width*height*format.channels);
        buffer.put(1,(byte)1);
    }

//    public Vector4f getRGBA(int x,int y){
//
//    }

    public Format getFormat() {
        return format;
    }
    public int getChannels(){
        return format.channels;
    }
    public int getFormatId(){
        return format.id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
