package com.systemvi.voxel.world.world;

public class BlockState {
    public Block block;
    public int lightLevel;


    public BlockState(Block block){
        this(block,0);
    }
    public BlockState(Block block,int lightLevel){
        this.block=block;
        this.lightLevel=lightLevel;
    }
}
