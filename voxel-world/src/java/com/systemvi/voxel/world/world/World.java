package com.systemvi.voxel.world.world;

import com.systemvi.engine.noise.Perlin2d;
import org.joml.Vector3i;

public class World {
    private Chunk[][][] chunks;
    private ChunkCache[][][] caches;

    private Perlin2d noise;

    public World(){
        noise=new Perlin2d((int)System.currentTimeMillis(),100,100);
        Vector3i worldSize=new Vector3i(8,8,8);
        chunks=new Chunk[worldSize.x][worldSize.y][worldSize.z];
        caches=new ChunkCache[worldSize.x][worldSize.y][worldSize.z];

        for(int i=0;i<worldSize.x;i++){
            for(int j=0;j<worldSize.y;j++){
                for(int k=0;k<worldSize.z;k++){
                    chunks[i][j][k]=new Chunk(new Vector3i(i,j,k),noise);
                }
            }
        }
        for(int i=0;i<worldSize.x;i++){
            for(int j=0;j<worldSize.y;j++){
                for(int k=0;k<worldSize.z;k++){
                    caches[i][j][k]=new ChunkCache(this,new Vector3i(i,j,k));
                }
            }
        }

//        generateFractal();
//        for(int i=0;i<chunks.length;i++){
//            for(int j=0;j<chunks[0].length;j++){
//                for(int k=0;k<chunks[0][0].length;k++){
//                    chunks[i][j][k].generateCache(new Vector3i(i,j,k),this);
//                }
//            }
//        }
    }

    public void generateFractal() {
//        getBlockState(50, 50, 50).block = Block.STONE;
        int maxSteps = 4;
        for (int i = 0; i < 81; i++) {
            for (int j = 0; j < 81; j++) {
                for (int k = 0; k < 81; k++) {
                    boolean isEmpty = false;
                    int size = 3;
                    for (int l = 1; l <= maxSteps; l++) {
                        int x = (i % size)/(size/3);
                        int y = (j % size)/(size/3);
                        int z = (k % size)/(size/3);
                        if ((x == 1 && y == 1)||(x == 1 && z == 1)||(z == 1 && y == 1)) {
                            isEmpty = true;
                        }
                        size *= 3;
                    }
                    if (isEmpty) {
                        getBlockState(i, j, k).block = Block.AIR;
                    } else {
                        getBlockState(i, j, k).block = Block.STONE;
                    }
                }
            }
        }
    }

    public Chunk[][][] getChunks() {
        return chunks;
    }
    public ChunkCache[][][] getCaches() {return caches;}
    public BlockState getBlockState(int x, int y, int z){
        int chunkX=x/ Chunk.SIZE_X;
        int chunkY=y/ Chunk.SIZE_Y;
        int chunkZ=z/ Chunk.SIZE_Z;
        x=x% Chunk.SIZE_X;
        y=y% Chunk.SIZE_Y;
        z=z% Chunk.SIZE_Z;
        if(chunkX<0||chunkX>=chunks.length||x<0)return new BlockState(Block.AIR);
        if(chunkY<0||chunkY>=chunks[0].length||y<0)return new BlockState(Block.AIR);
        if(chunkZ<0||chunkZ>=chunks[0][0].length||z<0)return new BlockState(Block.AIR);

        return chunks[chunkX][chunkY][chunkZ].blockStates[x][y][z];
    }

    public BlockState getBlockState(Vector3i pos){
        return getBlockState(pos.x, pos.y, pos.z);
    }

    public void setBlockState(BlockState state, int chunkX, int chunkY, int chunkZ, int x, int y, int z){
        chunks[chunkX][chunkY][chunkZ].blockStates[x][y][z]=state;
    }
}
