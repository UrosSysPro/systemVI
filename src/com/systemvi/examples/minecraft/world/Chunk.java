package com.systemvi.examples.minecraft.world;

import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Random;

public class Chunk {
    public static final int WIDTH=16,HEIGHT=16,DEPTH=16;


    public BlockState[][][] blockStates;
    public ArrayList<BlockFace> cache;

    public Chunk(Vector3i chunkPosition) {
        blockStates = new BlockState[WIDTH][HEIGHT][DEPTH];
        Random r = new Random();


            for (int i = 0; i < WIDTH; i++) {
                for (int k = 0; k < DEPTH; k++) {

                    int landLevel = r.nextInt(5)+3;
                    for (int j = 0; j < HEIGHT; j++) {
                    if (j < landLevel) {
                        blockStates[i][j][k] = new BlockState(Block.STONE);
                    } else {
                        blockStates[i][j][k] = new BlockState(Block.AIR);
                    }
//                    if(j<3)blockStates[i][j][k]=new BlockState(Block.STONE);
//                    if(j==3)blockStates[i][j][k]=new BlockState(Block.DIRT);
//                    if(j>3)blockStates[i][j][k]=new BlockState(Block.AIR);
                }
            }
        }
    }

    public void generateCache(){
        cache=new ArrayList<>();

        for(int i=0;i<WIDTH;i++){
            for(int j=0;j<HEIGHT;j++){
                for(int k=0;k<DEPTH;k++){
                    if(blockStates[i][j][k].block==Block.AIR)continue;
                    if(i-1<0||blockStates[i-1][j][k].block==Block.AIR){
                        cache.add(new BlockFace(
                            new Vector3i(i,j,k),
                            blockStates[i][j][k].block.region,
                            BlockFace.LEFT
                        ));
                    }
                    if(i+1>=WIDTH||blockStates[i+1][j][k].block==Block.AIR){
                        cache.add(new BlockFace(
                            new Vector3i(i,j,k),
                            blockStates[i][j][k].block.region,
                            BlockFace.RIGHT
                        ));
                    }
                    if(j-1<0||blockStates[i][j-1][k].block==Block.AIR){
                        cache.add(new BlockFace(
                            new Vector3i(i,j,k),
                            blockStates[i][j][k].block.region,
                            BlockFace.DOWN
                        ));
                    }
                    if(j+1>=HEIGHT||blockStates[i][j+1][k].block==Block.AIR){
                        cache.add(new BlockFace(
                            new Vector3i(i,j,k),
                            blockStates[i][j][k].block.region,
                            BlockFace.UP
                        ));
                    }
                    if(k-1<0||blockStates[i][j][k-1].block==Block.AIR){
                        cache.add(new BlockFace(
                            new Vector3i(i,j,k),
                            blockStates[i][j][k].block.region,
                            BlockFace.BACK
                        ));
                    }
                    if(k+1>=DEPTH||blockStates[i][j][k+1].block==Block.AIR){
                        cache.add(new BlockFace(
                            new Vector3i(i,j,k),
                            blockStates[i][j][k].block.region,
                            BlockFace.FRONT
                        ));
                    }
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
