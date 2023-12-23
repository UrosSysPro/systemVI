package com.systemvi.examples.minecraft.world;

import com.systemvi.engine.noise.Perlin2d;
import com.systemvi.examples.test3d.CameraController;
import org.joml.Vector3i;

public class World {
    private Chunk[][][] chunks;
    private WorldDebugRenderer debugRenderer;

    private Perlin2d noise;

    public World(){
        noise=new Perlin2d((int)System.currentTimeMillis(),100,100);
        chunks=new Chunk[5][2][5];
        for(int i=0;i<chunks.length;i++){
            for(int j=0;j<chunks[0].length;j++){
                for(int k=0;k<chunks[0][0].length;k++){
                    chunks[i][j][k]=new Chunk(new Vector3i(i,j,k),noise);
                }
            }
        }
        debugRenderer=new WorldDebugRenderer();
    }
    public void debugDraw(CameraController controller){
        debugRenderer.use(controller);
//        debugRenderer.draw(0,0,0);
//        debugRenderer.draw(2,4,1);
        for(int i=0;i<chunks.length;i++){
            for(int j=0;j<chunks[0].length;j++){
                for(int k=0;k<chunks[0][0].length;k++){
                    chunks[i][j][k].debugDraw(i,j,k,debugRenderer);
                }
            }
        }
    }
}
