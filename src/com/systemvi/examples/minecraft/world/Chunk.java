package com.systemvi.examples.minecraft.world;

import org.joml.Vector3i;

public class Chunk {
    public static final int WIDTH=16,HEIGHT=16,DEPTH=16;


    public BlockState[][][] blockStates;

    public Chunk(Vector3i chunkPosition){
        blockStates=new BlockState[WIDTH][HEIGHT][DEPTH];
        for(int i=0;i<WIDTH;i++){
            for(int j=0;j<HEIGHT;j++){
                for(int k=0;k<DEPTH;k++){
                    if(j<3)blockStates[i][j][k]=new BlockState(Block.STONE);
                    if(j==3)blockStates[i][j][k]=new BlockState(Block.DIRT);
                    if(j>3)blockStates[i][j][k]=new BlockState(Block.AIR);
                }
            }
        }
    }

    public void debugDraw(int x,int y,int z,WorldDebugRenderer debugRenderer){
        for(int i=0;i<WIDTH;i++){
            for(int j=0;j<HEIGHT;j++){
                for(int k=0;k<DEPTH;k++){
                    if(blockStates[i][j][k].block!=Block.AIR){
                        debugRenderer.draw(i+x*WIDTH,j+y*HEIGHT,k+z*DEPTH);
                    }
                }
            }
        }
    }
}
