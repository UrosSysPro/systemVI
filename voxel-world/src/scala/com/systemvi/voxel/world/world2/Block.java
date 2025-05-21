package com.systemvi.voxel.world.world2;

import com.systemvi.engine.texture.TextureRegion;

public class Block {
    public TextureRegion top,bottom,left,right,front,back;
    public boolean opaque = true;

    public Block(TextureRegion top,TextureRegion bottom,TextureRegion sides,boolean opaque){
        this(top,bottom,sides,sides,sides,sides,opaque);
    }

    public Block(TextureRegion top,TextureRegion bottom,TextureRegion left,TextureRegion right,TextureRegion front,TextureRegion back,boolean opaque){
        this.top=top;
        this.bottom=bottom;
        this.left=left;
        this.right=right;
        this.front=front;
        this.back=back;
        this.opaque=opaque;
    }

    public static Block AIR,STONE,DIRT;
}
