package com.systemvi.engine.texture;
import static org.lwjgl.opengl.GL33.*;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

public class Texture{
    private int id,width,height;
    private Format format;
    public Texture(String fileName){
        id=glGenTextures();
        glBindTexture(GL_TEXTURE_2D,id);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);


        int[] width=new int[1],height=new int[1],chanels=new int[1];
        ByteBuffer buffer=STBImage.stbi_load(fileName,width,height,chanels,0);
        if(buffer==null){
            System.out.println("[ERROR] Loading Image");
            return;
        }
        this.width=width[0];
        this.height=height[0];
        int channels=chanels[0];
        this.format=Format.R;
        if(channels==2)this.format=Format.RG;
        if(channels==3)this.format=Format.RGB;
        if(channels==4)this.format=Format.RGBA;

//        System.out.println("width: "+this.width);
//        System.out.println("height: "+this.height);
//        System.out.println("channels: "+this.format.channels);
//        System.out.println("sum: " +this.width*this.height*this.format.channels);
//        System.out.println("buffer size: "+buffer.capacity());

        glTexImage2D(GL_TEXTURE_2D,0,this.format.id,this.width,this.height,0,this.format.id,GL_UNSIGNED_BYTE,buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        glBindTexture(GL_TEXTURE_2D,0);
    }

    public Texture(int width,int height,Format format){
        this.width=width;
        this.height=height;
        this.format=format;
        id=glGenTextures();
        glBindTexture(GL_TEXTURE_2D,id);


        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D,0,this.format.id,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE, (ByteBuffer) null);
//        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D,0);
    }

    public Texture(){
        this.width=255;
        this.height=255;
        this.format=Format.RGBA;
        id=glGenTextures();
        glBindTexture(GL_TEXTURE_2D,id);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

//        ByteBuffer buffer=ByteBuffer.allocate(width*height*format.channels);
        ByteBuffer buffer=ByteBuffer.allocate((width)*(height)*format.channels);
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                int index=(i+j*width)*format.channels;
                buffer.put(index,(byte)i);
                buffer.put(index+1,(byte)j);
                buffer.put(index+2,(byte)128);
                buffer.put(index+3,(byte)255);
            }
        }
        glTexImage2D(GL_TEXTURE_2D,0,this.format.id,this.width,this.height,0,this.format.id,GL_UNSIGNED_BYTE,buffer);
//        glTexImage2D(GL_TEXTURE_2D,0,this.format.id,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D,0);
    }

    public Texture setData(TextureData data){

        glBindTexture(GL_TEXTURE_2D,id);

        glTexImage2D(GL_TEXTURE_2D,0,format.id,width,height,0,data.getFormat().id,GL_UNSIGNED_BYTE,data.getBuffer());
        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D,0);
        return this;
    }
    public void bind(int i){
        glActiveTexture(GL_TEXTURE0+i);
        glBindTexture(GL_TEXTURE_2D,id);
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
