package com.systemvi.engine.texture;

import com.systemvi.engine.utils.Utils;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

public class TextureData {
    private ByteBuffer buffer;
    private int width,height;
    private Vector4f color4f;

    private TextureData(){
        color4f=new Vector4f();
    }
    public TextureData(int width,int height,Format format){
        this();
        this.width=width;
        this.height=height;
        buffer= BufferUtils.createByteBuffer(width*height*format.channels);
    }
    public TextureData(String fileName){
        fileName= Utils.assetsFolder+fileName;
        int[] width=new int[1],height=new int[1],chanels=new int[1];
        ByteBuffer buffer= STBImage.stbi_load(fileName,width,height,chanels,0);
        if(buffer==null){
            System.out.println("[ERROR] Loading Image");
            return;
        }
        this.width=width[0];
        this.height=height[0];
        int channels=chanels[0];
        this.buffer=BufferUtils.createByteBuffer(buffer.capacity());
        this.buffer.put(buffer);
        STBImage.stbi_image_free(buffer);
    }

    //get texutre data using floats
    public Vector4f get(int x, int y){
        int index=(x+y*width)*4;

        color4f.x=(float) Byte.toUnsignedInt(buffer.get(index))/255;
        color4f.y=(float) Byte.toUnsignedInt(buffer.get(index+1))/255;
        color4f.z=(float) Byte.toUnsignedInt(buffer.get(index+2))/255;
        color4f.w=(float) Byte.toUnsignedInt(buffer.get(index+3))/255;
        return  color4f;
    }

    //set texture data using floats
    public TextureData set(int x, int y, Vector4f color){
        int index=(x+y*width)*4;
        buffer.put(index,(byte)(color.x*255));
        buffer.put(index+1,(byte)(color.y*255));
        buffer.put(index+2,(byte)(color.z*255));
        buffer.put(index+3,(byte)(color.w*255));
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
