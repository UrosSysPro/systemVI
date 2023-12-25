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
        chunks=new Chunk[40][2][40];
        for(int i=0;i<chunks.length;i++){
            for(int j=0;j<chunks[0].length;j++){
                for(int k=0;k<chunks[0][0].length;k++){
                    chunks[i][j][k]=new Chunk(new Vector3i(i,j,k),noise);
                    chunks[i][j][k].generateCache(new Vector3i(i,j,k));
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
