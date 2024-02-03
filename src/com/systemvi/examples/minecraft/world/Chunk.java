package com.systemvi.examples.minecraft.world;

import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.noise.Perlin2d;
import org.joml.Matrix4f;
import org.joml.Vector2f;
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
                    if((chunkPosition.y*SIZE_Y+j+3)<height)
                        blockStates[i][j][k]=new BlockState(Block.STONE);
                    else if((chunkPosition.y*SIZE_Y+j)<height)
                        blockStates[i][j][k]=new BlockState(Block.DIRT);
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
            +size, +size, 0, 0, 1,  1, 0, 0,   0, 1, 0,    1, 0,
            -size, -size, 0, 0, 1,  1, 0, 0,   0, 1, 0,    0, 1,
            +size, -size, 0, 0, 1,  1, 0, 0,   0, 1, 0,    1, 1,
            -size, +size, 0, 0, 1,  1, 0, 0,   0, 1, 0,    0, 0,
        });
        mesh.setIndices(new int[]{
            0,2,1,
            0,1,3
        });
        mesh.enableInstancing(
            new VertexAttribute("col0",4),
            new VertexAttribute("col1",4),
            new VertexAttribute("col2",4),
            new VertexAttribute("col3",4),
            new VertexAttribute("uv",2)
        );
        triangles=2;
    }

    public void generateCache(Vector3i chunkPosition,World world){
        ArrayList<Matrix4f> matrices=new ArrayList<>();
        ArrayList<Vector2f> uvs=new ArrayList<>();
        for(int i=0;i<SIZE_X;i++){
            for(int j=0;j<SIZE_Y;j++){
                for(int k=0;k<SIZE_Z;k++){
                    if(blockStates[i][j][k].block==Block.AIR)continue;
                    int x=i+SIZE_X*chunkPosition.x,y=j+SIZE_Y*chunkPosition.y,z=k+SIZE_Z*chunkPosition.z;
                    if(world.getBlockState(x-1,y,z).block==Block.AIR){
                        //left
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(-0.5f,0,0)
                                .rotateY((float)Math.toRadians(-90))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.left.x,block.left.y));
                    }
                    if(world.getBlockState(x+1,y,z).block==Block.AIR){
                        //right
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(0.5f,0,0)
                                .rotateY((float)Math.toRadians(90))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.right.x,block.right.y));
                    }
                    if(world.getBlockState(x,y-1,z).block==Block.AIR){
                        //down
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(0,-0.5f,0)
                                .rotateX((float)Math.toRadians(90))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.bottom.x,block.bottom.y));
                    }
                    if(world.getBlockState(x,y+1,z).block==Block.AIR){
                        //up
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(0,0.5f,0)
                                .rotateX((float)Math.toRadians(-90))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.top.x,block.top.y));
                    }
                    if(world.getBlockState(x,y,z-1).block==Block.AIR){
                        //back
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(0,0,-0.5f)
                                .rotateY((float)Math.toRadians(180))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.back.x,block.back.y));
                    }
                    if(world.getBlockState(x,y,z+1).block==Block.AIR){
                        //front
                        matrices.add(new Matrix4f()
                                .translate(x,y,z)
                                .translate(0,0,0.5f)
                                .rotateY((float)Math.toRadians(0))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.front.x,block.front.y));
                    }
                }
            }
        }
        int matrixSize=16;
        int uvSize=2;
        int instanceSize=matrixSize+uvSize;
        float[] instanceData=new float[matrices.size()*instanceSize];
        float[] matrixData=new float[matrixSize];
        float[] uvData=new float[uvSize];
        for(int i=0;i<matrices.size();i++){
            matrices.get(i).get(matrixData);
            uvData[0]=uvs.get(i).x;
            uvData[1]=uvs.get(i).y;
            for(int j=0;j<matrixSize;j++){
                instanceData[i*instanceSize+j]=matrixData[j];
            }
            for(int j=0;j<uvSize;j++){
                instanceData[i*instanceSize+matrixSize+j]=uvData[j];
            }
        }
        instancesToDraw=matrices.size();
        mesh.setInstanceData(instanceData);
    }

}
