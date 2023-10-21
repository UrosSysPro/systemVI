package com.systemvi.engine.texture;
import static org.lwjgl.opengl.GL33.*;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

public class Texture{
    private int id,width,height,channels;
//    public Texture(){
//        id=glGenTextures();
//        glBindTexture(GL_TEXTURE_2D,id);
//    }
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
        this.channels=chanels[0];
        glTexImage2D(GL_TEXTURE_2D,0,GL_RGB,width[0],height[0],0,GL_RGB,GL_UNSIGNED_BYTE,buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        glBindTexture(GL_TEXTURE_2D,0);
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
        return channels;
    }
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
