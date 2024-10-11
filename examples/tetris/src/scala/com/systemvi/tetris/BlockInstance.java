package com.systemvi.tetris;

public class BlockInstance {
    public Block block;
    public int x,y;
    public BlockInstance(Block block,int x,int y){
        this.block=block;
        this.x=x;
        this.y=y;
    }
}
