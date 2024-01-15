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

    //get texutre data using floats
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
    public float getPixel1f(int x,int y){
        int index=(x+y*width)*format.channels;
        color1f=(float) Byte.toUnsignedInt(buffer.get(index))/255;
        return  color1f;
    }


    //get texutre data using ints
    public Vector4i getPixel4i(int x,int y){
        int index=(x+y*width)*format.channels;

        color4i.x=Byte.toUnsignedInt(buffer.get(index));
        color4i.y=Byte.toUnsignedInt(buffer.get(index+1));
        color4i.z=Byte.toUnsignedInt(buffer.get(index+2));
        color4i.w=Byte.toUnsignedInt(buffer.get(index+3));
        return  color4i;
    }
    public Vector3i getPixel3i(int x,int y){
        int index=(x+y*width)*format.channels;

        color3i.x=Byte.toUnsignedInt(buffer.get(index));
        color3i.y=Byte.toUnsignedInt(buffer.get(index+1));
        color3i.z=Byte.toUnsignedInt(buffer.get(index+2));
        return  color3i;
    }
    public Vector2i getPixel2i(int x,int y){
        int index=(x+y*width)*format.channels;
        color2i.x= Byte.toUnsignedInt(buffer.get(index));
        color2i.y= Byte.toUnsignedInt(buffer.get(index+1));
        return  color2i;
    }
    public int getPixel1i(int x,int y){
        int index=(x+y*width)*format.channels;
        color1i= Byte.toUnsignedInt(buffer.get(index));
        return  color1i;
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

    //set texture data using floats
    public TextureData setPixel4f(int x,int y,Vector4f color){
        int index=(x+y*width)*format.channels;

        buffer.put(index,(byte)(color.x*255));
        buffer.put(index+1,(byte)(color.y*255));
        buffer.put(index+2,(byte)(color.z*255));
        buffer.put(index+3,(byte)(color.w*255));
        return  this;
    }
    public TextureData setPixel3f(int x,int y,Vector3f color){
        int index=(x+y*width)*format.channels;

        buffer.put(index,(byte)(color.x*255));
        buffer.put(index+1,(byte)(color.y*255));
        buffer.put(index+2,(byte)(color.z*255));
        return  this;
    }
    public TextureData setPixel2f(int x,int y,Vector2f color){
        int index=(x+y*width)*format.channels;

        buffer.put(index,(byte)(color.x*255));
        buffer.put(index+1,(byte)(color.y*255));
        return  this;
    }
    public TextureData setPixel1f(int x,int y,float color){
        int index=(x+y*width)*format.channels;

        buffer.put(index,(byte)(color*255));
        return  this;
    }

    //set texture data using ints

    public TextureData setPixel4i(int x,int y,Vector4i color){
        int index=(x+y*width)*format.channels;

        buffer.put(index,(byte)color.x);
        buffer.put(index+1,(byte)color.y);
        buffer.put(index+2,(byte)color.z);
        buffer.put(index+3,(byte)color.w);
        return  this;
    }
    public TextureData setPixel3i(int x,int y,Vector3i color){
        int index=(x+y*width)*format.channels;

        buffer.put(index,(byte)color.x);
        buffer.put(index+1,(byte)color.y);
        buffer.put(index+2,(byte)color.z);
        return  this;
    }
    public TextureData setPixel2i(int x,int y,Vector2i color){
        int index=(x+y*width)*format.channels;

        buffer.put(index,(byte)color.x);
        buffer.put(index+1,(byte)color.y);
        return  this;
    }
    public TextureData setPixel1i(int x,int y,float color){
        int index=(x+y*width)*format.channels;

        buffer.put(index,(byte)color);
        return  this;
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
