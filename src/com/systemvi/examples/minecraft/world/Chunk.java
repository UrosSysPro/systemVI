package com.systemvi.examples.minecraft.world;

import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.noise.Perlin2d;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Random;

public class Chunk {
    public static final int WIDTH=16,HEIGHT=16,DEPTH=16;


    public BlockState[][][] blockStates;
    public Mesh mesh;
    public int triangles;
    public int instancesToDraw;
//    public ArrayList<BlockFace> cache;

    public Chunk(Vector3i chunkPosition, Perlin2d noise) {
        blockStates = new BlockState[WIDTH][HEIGHT][DEPTH];
        Random r = new Random();
        for (int i = 0; i < WIDTH; i++) {
            for (int k = 0; k < DEPTH; k++) {
                int height = (int) (noise.get(
                    (float) (chunkPosition.x * WIDTH + i) / 40,
                    (float) (chunkPosition.z * DEPTH + k) / 40
                ) * 30) + 1;
                for (int j = 0; j < HEIGHT; j++) {
                    if((chunkPosition.y*HEIGHT+j)<height)
                        blockStates[i][j][k]=new BlockState(Block.STONE);
                    else
                        blockStates[i][j][k]=new BlockState(Block.AIR);
                }
            }
        }
        mesh=new Mesh(
            new VertexAttribute("position",2),
            new VertexAttribute("normal",3),
            new VertexAttribute("tangent",3),
            new VertexAttribute("bitangent",3),
            new VertexAttribute("texCoords",2)
        );
        float size=0.5f;
        mesh.setVertexData(new float[]{
            //position    //normal  //tangent  //bitangent //texCoords
            +size, +size, 0, 0, 1,  1, 0, 0,   0, 1, 0,    1, 1,
            -size, -size, 0, 0, 1,  1, 0, 0,   0, 1, 0,    0, 0,
            +size, -size, 0, 0, 1,  1, 0, 0,   0, 1, 0,    1, 0,
            -size, +size, 0, 0, 1,  1, 0, 0,   0, 1, 0,    0, 1,
        });
        mesh.setIndices(new int[]{
            0,2,1,
            0,1,3
        });
        mesh.enableInstancing(
            new VertexAttribute("col0",4),
            new VertexAttribute("col1",4),
            new VertexAttribute("col2",4),
            new VertexAttribute("col3",4)
        );
        triangles=2;
    }

    public void generateCache(Vector3i chunkPosition){
        ArrayList<Matrix4f> matrices=new ArrayList<>();
        for(int i=0;i<WIDTH;i++){
            for(int j=0;j<HEIGHT;j++){
                for(int k=0;k<DEPTH;k++){
                    if(blockStates[i][j][k].block==Block.AIR)continue;
                    if(i-1<0||blockStates[i-1][j][k].block==Block.AIR){
                        //left
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*WIDTH+i,
                                chunkPosition.y*HEIGHT+j,
                                chunkPosition.z*DEPTH+k
                            )
                            .translate(-0.5f,0,0)
                            .rotateY((float)Math.toRadians(-90))
                        );
                    }
                    if(i+1>=WIDTH||blockStates[i+1][j][k].block==Block.AIR){
                        //right
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*WIDTH+i,
                                chunkPosition.y*HEIGHT+j,
                                chunkPosition.z*DEPTH+k
                            )
                            .translate(0.5f,0,0)
                            .rotateY((float)Math.toRadians(90))
                        );
                    }
                    if(j-1<0||blockStates[i][j-1][k].block==Block.AIR){
                        //down
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*WIDTH+i,
                                chunkPosition.y*HEIGHT+j,
                                chunkPosition.z*DEPTH+k
                            )
                            .translate(0,-0.5f,0)
                            .rotateX((float)Math.toRadians(90))
                        );
                    }
                    if(j+1>=HEIGHT||blockStates[i][j+1][k].block==Block.AIR){
                        //up
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*WIDTH+i,
                                chunkPosition.y*HEIGHT+j,
                                chunkPosition.z*DEPTH+k
                            )
                            .translate(0,0.5f,0)
                            .rotateX((float)Math.toRadians(-90))
                        );
                    }
                    if(k-1<0||blockStates[i][j][k-1].block==Block.AIR){
                       //back
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*WIDTH+i,
                                chunkPosition.y*HEIGHT+j,
                                chunkPosition.z*DEPTH+k
                            )
                            .translate(0,0,-0.5f)
                            .rotateY((float)Math.toRadians(180))
                        );
                    }
                    if(k+1>=DEPTH||blockStates[i][j][k+1].block==Block.AIR){
                        //front
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*WIDTH+i,
                                chunkPosition.y*HEIGHT+j,
                                chunkPosition.z*DEPTH+k
                            )
                            .translate(0,0,0.5f)
                            .rotateY((float)Math.toRadians(0))
                        );
                    }
                }
            }
        }
        int matrixSize=16;
        float[] instanceData=new float[matrices.size()*matrixSize];
        float[] matrixData=new float[matrixSize];
        for(int i=0;i<matrices.size();i++){
            matrices.get(i).get(matrixData);
            for(int j=0;j<matrixSize;j++){
                instanceData[i*matrixSize+j]=matrixData[j];
            }
        }
        instancesToDraw=matrices.size();
        mesh.setInstanceData(instanceData);
    }

//    public void generateCache(){
//        cache=new ArrayList<>();
//
//        for(int i=0;i<WIDTH;i++){
//            for(int j=0;j<HEIGHT;j++){
//                for(int k=0;k<DEPTH;k++){
//                    if(blockStates[i][j][k].block==Block.AIR)continue;
//                    if(i-1<0||blockStates[i-1][j][k].block==Block.AIR){
//                        cache.add(new BlockFace(
//                            new Vector3i(i,j,k),
//                            blockStates[i][j][k].block.region,
//                            BlockFace.LEFT
//                        ));
//                    }
//                    if(i+1>=WIDTH||blockStates[i+1][j][k].block==Block.AIR){
//                        cache.add(new BlockFace(
//                            new Vector3i(i,j,k),
//                            blockStates[i][j][k].block.region,
//                            BlockFace.RIGHT
//                        ));
//                    }
//                    if(j-1<0||blockStates[i][j-1][k].block==Block.AIR){
//                        cache.add(new BlockFace(
//                            new Vector3i(i,j,k),
//                            blockStates[i][j][k].block.region,
//                            BlockFace.DOWN
//                        ));
//                    }
//                    if(j+1>=HEIGHT||blockStates[i][j+1][k].block==Block.AIR){
//                        cache.add(new BlockFace(
//                            new Vector3i(i,j,k),
//                            blockStates[i][j][k].block.region,
//                            BlockFace.UP
//                        ));
//                    }
//                    if(k-1<0||blockStates[i][j][k-1].block==Block.AIR){
//                        cache.add(new BlockFace(
//                            new Vector3i(i,j,k),
//                            blockStates[i][j][k].block.region,
//                            BlockFace.BACK
//                        ));
//                    }
//                    if(k+1>=DEPTH||blockStates[i][j][k+1].block==Block.AIR){
//                        cache.add(new BlockFace(
//                            new Vector3i(i,j,k),
//                            blockStates[i][j][k].block.region,
//                            BlockFace.FRONT
//                        ));
//                    }
//                }
//            }
//        }
//    }

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
