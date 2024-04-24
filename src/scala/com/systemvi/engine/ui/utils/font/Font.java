package com.systemvi.engine.ui.utils.font;


import com.google.gson.Gson;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.Utils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

public class Font {
    public class Config{
        public Config(){}
        public int base,
            bold,
            charHeight,
            charSpacing,
            italic,
            lineSpacing,
        size,smooth,textureWidth,textureHeight;
        public String face,textureFile;
    }
    public class Symbol{
        public Symbol(){}
        public int id,x,y, width,height,xoffset,yoffset,xadvance;
    }
    public Config config;
    public Symbol[] symbols;
    public Texture texture;

    public Font(){

    }
    public static Font load(String textureFile,String jsonFile){
        Texture texture=new Texture(textureFile);
        String jsonData= Utils.readExternal(jsonFile);
        Gson gson=new Gson();
        Font font=gson.fromJson(jsonData,Font.class);
        font.texture=texture;
        texture.generateMipMaps();
        texture.setSamplerFilter(GL33.GL_NEAREST_MIPMAP_LINEAR,GL33.GL_NEAREST);
        return font;
    }
}
