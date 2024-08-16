package com.systemvi.engine.texture;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;
public class CubeMap {
    private final int id;
    public CubeMap(String[] faces){
        id=glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        ByteBuffer data;
        int[] formats=new int[]{GL_R,GL_RG,GL_RGB,GL_RGBA};
        for(int i = 0; i < faces.length; i++) {
            int[] width=new int[1],height=new int[1],channels=new int[1];
            data = stbi_load(faces[i], width,height,channels,0);
            glTexImage2D(
                    GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                    0, GL_RGB, width[0], height[0],
                    0, formats[channels[0]-1], GL_UNSIGNED_BYTE, data
            );
            stbi_image_free(data);
        }
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
    }

    public void bind(int i){
        glActiveTexture(i);
        glBindTexture(GL_TEXTURE_CUBE_MAP,id);
    }
    public void bind(){
        bind(0);
    }
}
