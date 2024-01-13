package com.systemvi.examples.minecraft.world;

import com.systemvi.engine.noise.Perlin2d;
import com.systemvi.examples.minecraft.renderer.BlockFaceRenderer;
import com.systemvi.examples.test3d.CameraController;
import org.joml.Vector3i;

public class World {
    private Chunk[][][] chunks;
    private WorldDebugRenderer debugRenderer;
    private BlockFaceRenderer renderer;

    private Perlin2d noise;

    public World(){
        renderer=new BlockFaceRenderer();
        noise=new Perlin2d((int)System.currentTimeMillis(),100,100);
        chunks=new Chunk[15][2][15];
        for(int i=0;i<chunks.length;i++){
            for(int j=0;j<chunks[0].length;j++){
                for(int k=0;k<chunks[0][0].length;k++){
                    chunks[i][j][k]=new Chunk(new Vector3i(i,j,k),noise);
                }
            }
        }
        for(int i=0;i<chunks.length;i++){
            for(int j=0;j<chunks[0].length;j++){
                for(int k=0;k<chunks[0][0].length;k++){
                    chunks[i][j][k].generateCache(new Vector3i(i,j,k),this);
//                    chunks[i][j][k].generateCache(new Vector3i(i,j,k));
                }
            }
        }
        debugRenderer=new WorldDebugRenderer();
    }

    public BlockState getBlockState(int x,int y,int z){
        int chunkX=x/Chunk.SIZE_X;
        int chunkY=y/Chunk.SIZE_Y;
        int chunkZ=z/Chunk.SIZE_Z;
        x=x%Chunk.SIZE_X;
        y=y%Chunk.SIZE_Y;
        z=z%Chunk.SIZE_Z;
        if(chunkX<0||chunkX>=chunks.length||x<0)return new BlockState(Block.AIR);
        if(chunkY<0||chunkY>=chunks[0].length||y<0)return new BlockState(Block.AIR);
        if(chunkZ<0||chunkZ>=chunks[0][0].length||z<0)return new BlockState(Block.AIR);

        return chunks[chunkX][chunkY][chunkZ].blockStates[x][y][z];
    }

    public BlockState getBlockState(Vector3i pos){
        return getBlockState(pos.x, pos.y, pos.z);
    }
    public void debugDraw(CameraController controller){
        debugRenderer.use(controller);
        for(int i=0;i<chunks.length;i++){
            for(int j=0;j<chunks[0].length;j++){
                for(int k=0;k<chunks[0][0].length;k++){
                    chunks[i][j][k].debugDraw(i,j,k,debugRenderer);
                }
            }
        }
    }
    public void draw(CameraController controller){
        renderer.use();
        renderer.setCamera(controller.camera);
        for(int i=0;i<chunks.length;i++){
            for(int j=0;j<chunks[0].length;j++){
                for(int k=0;k<chunks[0][0].length;k++){
                    renderer.drawChunk(chunks[i][j][k]);
                }
            }
        }
    }
}
