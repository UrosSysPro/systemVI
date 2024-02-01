package com.systemvi.voxel.world.world;

import com.systemvi.engine.texture.TextureRegion;

public class Block {
    public TextureRegion region;

    public Block(TextureRegion region){
        this.region=region;
    }

    public static Block AIR=new Block(null),STONE=new Block(null),DIRT=new Block(null);
}
