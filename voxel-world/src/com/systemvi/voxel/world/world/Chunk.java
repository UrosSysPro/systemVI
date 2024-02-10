package com.systemvi.voxel.world.world;

import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.noise.Perlin2d;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Random;

public class Chunk {
    public static final int SIZE_X=16,SIZE_Y=16,SIZE_Z=16;


    public BlockState[][][] blockStates;
    public Mesh mesh;
    public int triangles;
    public int instancesToDraw;
//    public ArrayList<BlockFace> cache;

    public Chunk(Vector3i chunkPosition, Perlin2d noise) {
        blockStates = new BlockState[SIZE_X][SIZE_Y][SIZE_Z];
        Random r = new Random();
        for (int i = 0; i < SIZE_X; i++) {
            for (int k = 0; k < SIZE_Z; k++) {
                int height = (int) (noise.get(
                    (float) (chunkPosition.x * SIZE_X + i) / 40,
                    (float) (chunkPosition.z * SIZE_Z + k) / 40
                ) * 30) + 1;
                for (int j = 0; j < SIZE_Y; j++) {
                    if((chunkPosition.y*SIZE_Y+j)<height)
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
        for(int i=0;i<SIZE_X;i++){
            for(int j=0;j<SIZE_Y;j++){
                for(int k=0;k<SIZE_Z;k++){
                    if(blockStates[i][j][k].block== Block.AIR)continue;
                    if(i-1<0||blockStates[i-1][j][k].block== Block.AIR){
                        //left
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*SIZE_X+i,
                                chunkPosition.y*SIZE_Y+j,
                                chunkPosition.z*SIZE_Z+k
                            )
                            .translate(-0.5f,0,0)
                            .rotateY((float)Math.toRadians(-90))
                        );
                    }
                    if(i+1>=SIZE_X||blockStates[i+1][j][k].block== Block.AIR){
                        //right
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*SIZE_X+i,
                                chunkPosition.y*SIZE_Y+j,
                                chunkPosition.z*SIZE_Z+k
                            )
                            .translate(0.5f,0,0)
                            .rotateY((float)Math.toRadians(90))
                        );
                    }
                    if(j-1<0||blockStates[i][j-1][k].block== Block.AIR){
                        //down
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*SIZE_X+i,
                                chunkPosition.y*SIZE_Y+j,
                                chunkPosition.z*SIZE_Z+k
                            )
                            .translate(0,-0.5f,0)
                            .rotateX((float)Math.toRadians(90))
                        );
                    }
                    if(j+1>=SIZE_Y||blockStates[i][j+1][k].block== Block.AIR){
                        //up
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*SIZE_X+i,
                                chunkPosition.y*SIZE_Y+j,
                                chunkPosition.z*SIZE_Z+k
                            )
                            .translate(0,0.5f,0)
                            .rotateX((float)Math.toRadians(-90))
                        );
                    }
                    if(k-1<0||blockStates[i][j][k-1].block== Block.AIR){
                       //back
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*SIZE_X+i,
                                chunkPosition.y*SIZE_Y+j,
                                chunkPosition.z*SIZE_Z+k
                            )
                            .translate(0,0,-0.5f)
                            .rotateY((float)Math.toRadians(180))
                        );
                    }
                    if(k+1>=SIZE_Z||blockStates[i][j][k+1].block== Block.AIR){
                        //front
                        matrices.add(new Matrix4f()
                            .translate(
                                chunkPosition.x*SIZE_X+i,
                                chunkPosition.y*SIZE_Y+j,
                                chunkPosition.z*SIZE_Z+k
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
    public void generateCache(Vector3i chunkPosition, World world){
        ArrayList<Matrix4f> matrices=new ArrayList<>();
        for(int i=0;i<SIZE_X;i++){
            for(int j=0;j<SIZE_Y;j++){
                for(int k=0;k<SIZE_Z;k++){
                    if(blockStates[i][j][k].block== Block.AIR)continue;
                    int x=i+SIZE_X*chunkPosition.x,y=j+SIZE_Y*chunkPosition.y,z=k+SIZE_Z*chunkPosition.z;
                    if(world.getBlockState(x-1,y,z).block== Block.AIR){
                        //left
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(-0.5f,0,0)
                                .rotateY((float)Math.toRadians(-90))
                        );
                    }
                    if(world.getBlockState(x+1,y,z).block== Block.AIR){
                        //right
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(0.5f,0,0)
                                .rotateY((float)Math.toRadians(90))
                        );
                    }
                    if(world.getBlockState(x,y-1,z).block== Block.AIR){
                        //down
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(0,-0.5f,0)
                                .rotateX((float)Math.toRadians(90))
                        );
                    }
                    if(world.getBlockState(x,y+1,z).block== Block.AIR){
                        //up
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(0,0.5f,0)
                                .rotateX((float)Math.toRadians(-90))
                        );
                    }
                    if(world.getBlockState(x,y,z-1).block== Block.AIR){
                        //back
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(0,0,-0.5f)
                                .rotateY((float)Math.toRadians(180))
                        );
                    }
                    if(world.getBlockState(x,y,z+1).block== Block.AIR){
                        //front
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
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
//        for(int i=0;i<SIZE_X;i++){
//            for(int j=0;j<SIZE_Y;j++){
//                for(int k=0;k<SIZE_Z;k++){
//                    if(blockStates[i][j][k].block==Block.AIR)continue;
//                    if(i-1<0||blockStates[i-1][j][k].block==Block.AIR){
//                        cache.add(new BlockFace(
//                            new Vector3i(i,j,k),
//                            blockStates[i][j][k].block.region,
//                            BlockFace.LEFT
//                        ));
//                    }
//                    if(i+1>=SIZE_X||blockStates[i+1][j][k].block==Block.AIR){
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
//                    if(j+1>=SIZE_Y||blockStates[i][j+1][k].block==Block.AIR){
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
//                    if(k+1>=SIZE_Z||blockStates[i][j][k+1].block==Block.AIR){
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

}