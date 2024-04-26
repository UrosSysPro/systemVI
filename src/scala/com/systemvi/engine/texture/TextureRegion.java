package com.systemvi.engine.texture;

public class TextureRegion {
    public Texture texture;
    public int x,y, width, height;
    public TextureRegion(Texture texture,int x,int y,int width,int height){
        this.texture=texture;
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }
    public float getTop(){
        return (float)y/texture.getHeight();
    }
    public float getBottom(){
        return (float)(y+height)/texture.getHeight();
    }
    public float getLeft(){
        return (float)x/texture.getWidth();
    }
    public float getRight(){
        return (float)(x+width)/texture.getWidth();
    }

    public Texture texture() {
        return texture;
    }

    public static TextureRegion[][] split(Texture texture, int tileWidth, int tileHeight){
        int n=texture.getWidth()/tileWidth;
        int m=texture.getHeight()/tileHeight;
        TextureRegion[][] regions=new TextureRegion[n][m];
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                regions[i][j]=new TextureRegion(texture,i*tileWidth,j*tileHeight,tileWidth,tileHeight);
            }
        }
        return regions;
    }
}
