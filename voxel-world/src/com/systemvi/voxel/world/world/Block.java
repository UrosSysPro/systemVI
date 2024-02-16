package com.systemvi.voxel.world.world;

import com.systemvi.engine.texture.TextureRegion;

public class Block {
    public TextureRegion top,bottom,left,right,front,back;

    public Block(TextureRegion top,TextureRegion bottom,TextureRegion sides){
        this(top,bottom,sides,sides,sides,sides);
    }
    public Block(TextureRegion top,TextureRegion bottom,TextureRegion left,TextureRegion right,TextureRegion front,TextureRegion back){
        this.top=top;
        this.bottom=bottom;
        this.left=left;
        this.right=right;
        this.front=front;
        this.back=back;
    }

    public static Block AIR,STONE,DIRT;
}
