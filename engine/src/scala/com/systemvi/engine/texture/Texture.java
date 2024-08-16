package com.systemvi.engine.texture;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL42.glBindImageTexture;

import com.systemvi.engine.utils.Utils;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

public class Texture{
    private final int id;
    private int width;
    private int height;
    private Format format;
    private Texture(){
        id=glGenTextures();
        setRepeat(GL_REPEAT,GL_REPEAT);
        setSamplerFilter(GL_NEAREST,GL_NEAREST);
    }
    public Texture(String fileName){
        id=glGenTextures();
        setRepeat(GL_REPEAT,GL_REPEAT);
        setSamplerFilter(GL_NEAREST,GL_NEAREST);
        loadFromFile(fileName);
    }

    public Texture(int width,int height,Format format){
        this.width=width;
        this.height=height;
        this.format=format;
        id=glGenTextures();

        setRepeat(GL_REPEAT,GL_REPEAT);
        setSamplerFilter(GL_NEAREST,GL_NEAREST);

        glBindTexture(GL_TEXTURE_2D,id);
        switch(format.id){
            case GL_DEPTH_COMPONENT:
            case GL_DEPTH_COMPONENT16:
            case GL_DEPTH_COMPONENT24:
            case GL_DEPTH_COMPONENT32:{
                glTexImage2D(GL_TEXTURE_2D,0,GL_DEPTH_COMPONENT24,width,height,0,GL_DEPTH_COMPONENT,GL_FLOAT,(ByteBuffer) null);
            }break;
            default:{
                glTexImage2D(GL_TEXTURE_2D,0,this.format.id,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE, (ByteBuffer) null);
            }break;
        }
        glBindTexture(GL_TEXTURE_2D,0);
    }

    public Texture setData(TextureData data){
        width=data.getWidth();
        height=data.getHeight();

        glBindTexture(GL_TEXTURE_2D,id);
        glTexImage2D(GL_TEXTURE_2D,0,this.format.id,width,height,0,Format.RGBA.id,GL_UNSIGNED_BYTE,data.getBuffer());
        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D,0);
        return this;
    }

    public Texture loadFromFile(String fileName){
        glBindTexture(GL_TEXTURE_2D,id);

        int[] width=new int[1],height=new int[1],chanels=new int[1];
        ByteBuffer buffer=STBImage.stbi_load(Utils.assetsFolder+fileName,width,height,chanels,0);
        if(buffer==null){
            System.out.println("[ERROR] Loading Image");
            return this;
        }
        this.width=width[0];
        this.height=height[0];
        int channels=chanels[0];
        this.format=Format.R;
        if(channels==2)this.format=Format.RG;
        if(channels==3)this.format=Format.RGB;
        if(channels==4)this.format=Format.RGBA;

        glTexImage2D(GL_TEXTURE_2D,0,this.format.id,this.width,this.height,0,this.format.id,GL_UNSIGNED_BYTE,buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        glBindTexture(GL_TEXTURE_2D,0);
        return this;
    }
    public void bind(){
        bind(0);
    }
    public void bind(int i){
        glActiveTexture(GL_TEXTURE0+i);
        glBindTexture(GL_TEXTURE_2D,id);
    }

    public void bindAsImage(int i){
        glActiveTexture(GL_TEXTURE0+i);
        glBindTexture(GL_TEXTURE_2D,id);
        glBindImageTexture(i, id, 0, false, 0, GL_READ_WRITE, format.id);
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannels() {
        return format.channels;
    }
    public Format getFormat(){return format;}
    public void setBorderColor(float r,float g,float b,float a){
        float[] borderColor=new float[]{r,g,b,a};
        glBindTexture(GL_TEXTURE_2D,id);
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        glBindTexture(GL_TEXTURE_2D,0);
    }
    public void setRepeat(int horizontal,int vertical){
        glBindTexture(GL_TEXTURE_2D,id);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,horizontal);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,vertical);
        glBindTexture(GL_TEXTURE_2D,0);
    }
    public void setSamplerFilter(int min,int mag){
        glBindTexture(GL_TEXTURE_2D,id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag);
        glBindTexture(GL_TEXTURE_2D,0);
    }

    public void generateMipMaps(){
        glBindTexture(GL_TEXTURE_2D,id);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D,0);
    }
    public void delete(){
        glBindTexture(GL_TEXTURE_2D,0);
        glDeleteTextures(id);
    }
}
