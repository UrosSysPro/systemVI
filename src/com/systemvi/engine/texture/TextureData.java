package com.systemvi.engine.texture;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class TextureData {
    private ByteBuffer buffer;
    private int width,height;
    private Format format;
    private Vector4f color4f;
    private Vector3f color3f;
    private Vector2f color2f;
    private float color1f;
    private Vector4i color4i;
    private Vector3i color3i;
    private Vector2i color2i;
    private int color1i;

    public TextureData(int width,int height,Format format){
        this.width=width;
        this.height=height;
        this.format=format;
        buffer= ByteBuffer.allocate(width*height*format.channels);

        color4f=new Vector4f();
        color3f=new Vector3f();
        color2f=new Vector2f();
        color1f=0;
        color4i=new Vector4i();
        color3i=new Vector3i();
        color2i=new Vector2i();
        color1i=0;
    }

    public Vector4f getPixel4f(int x,int y){
        int index=(x+y*width)*format.channels;

        color4f.x=(float) Byte.toUnsignedInt(buffer.get(index))/255;
        color4f.y=(float) Byte.toUnsignedInt(buffer.get(index+1))/255;
        color4f.z=(float) Byte.toUnsignedInt(buffer.get(index+2))/255;
        color4f.w=(float) Byte.toUnsignedInt(buffer.get(index+3))/255;
        return  color4f;
    }
    public Vector3f getPixel3f(int x,int y){
        int index=(x+y*width)*format.channels;

        color3f.x=(float) Byte.toUnsignedInt(buffer.get(index))/255;
        color3f.y=(float) Byte.toUnsignedInt(buffer.get(index+1))/255;
        color3f.z=(float) Byte.toUnsignedInt(buffer.get(index+2))/255;
        return  color3f;
    }
    public Vector2f getPixel2f(int x,int y){
        int index=(x+y*width)*format.channels;

        color2f.x=(float) Byte.toUnsignedInt(buffer.get(index))/255;
        color2f.y=(float) Byte.toUnsignedInt(buffer.get(index+1))/255;
        return  color2f;
    }

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
