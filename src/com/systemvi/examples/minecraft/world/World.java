package com.systemvi.examples.minecraft.world;

import com.systemvi.examples.test3d.CameraController;
import org.joml.Vector3i;

public class World {
    private Chunk[][][] chunks;
    private WorldDebugRenderer debugRenderer;

    public World(){
        chunks=new Chunk[2][1][2];
        for(int i=0;i<chunks.length;i++){
            for(int j=0;j<chunks[0].length;j++){
                for(int k=0;k<chunks[0][0].length;k++){
                    chunks[i][j][k]=new Chunk(new Vector3i(i,j,k));
                }
            }
        }
        debugRenderer=new WorldDebugRenderer();
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
}
