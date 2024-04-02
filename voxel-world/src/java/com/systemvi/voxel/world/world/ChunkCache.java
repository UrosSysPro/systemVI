package com.systemvi.voxel.world.world;

import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import java.util.ArrayList;

public class ChunkCache {
    public Mesh mesh;
    public World world;
    public Vector3i chunkPosition;
    public int triangles,instancesToDraw;
    public ChunkCache(World world,Vector3i chunkPosition){
        this.world=world;
        this.chunkPosition=chunkPosition;

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
            new VertexAttribute("uv",2),
            new VertexAttribute("lightLevel",4)
        );
        triangles=2;
        instancesToDraw=0;
        regenerate(world,chunkPosition);
    }
    public void regenerate(World world,Vector3i chunkPosition){
        ArrayList<Matrix4f> matrices=new ArrayList<>();
        ArrayList<Vector2f> uvs=new ArrayList<>();
        ArrayList<Vector4f> lightLevels=new ArrayList<>();
        Chunk chunk=world.getChunks()[chunkPosition.x][chunkPosition.y][chunkPosition.z];
        BlockState[][][] blockStates=chunk.blockStates;
        for(int i=0;i<Chunk.SIZE_X;i++){
            for(int j=0;j<Chunk.SIZE_Y;j++){
                for(int k=0;k<Chunk.SIZE_Z;k++){
                    if(blockStates[i][j][k].block== Block.AIR)continue;
                    int x=i+Chunk.SIZE_X*chunkPosition.x,y=j+Chunk.SIZE_Y*chunkPosition.y,z=k+Chunk.SIZE_Z*chunkPosition.z;
                    if(world.getBlockState(x-1,y,z).block== Block.AIR){
                        //left
                        matrices.add(new Matrix4f()
                            .translate(x,y,z)
                            .translate(-0.5f,0,0)
                            .rotateY((float)Math.toRadians(-90))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.left.x,block.left.y));
                        lightLevels.add(new Vector4f(world.getBlockState(x-1,y,z).lightLevel));
                    }
                    if(world.getBlockState(x+1,y,z).block== Block.AIR){
                        //right
                        matrices.add(new Matrix4f()
                            .translate(x,y,z)
                            .translate(0.5f,0,0)
                            .rotateY((float)Math.toRadians(90))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.right.x,block.right.y));
                        lightLevels.add(new Vector4f(world.getBlockState(x+1,y,z).lightLevel));
                    }
                    if(world.getBlockState(x,y-1,z).block== Block.AIR){
                        //down
                        matrices.add(new Matrix4f()
                            .translate(x,y,z)
                            .translate(0,-0.5f,0)
                            .rotateX((float)Math.toRadians(90))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.bottom.x,block.bottom.y));
                        lightLevels.add(new Vector4f(world.getBlockState(x,y-1,z).lightLevel));
                    }
                    if(world.getBlockState(x,y+1,z).block== Block.AIR){
                        //up
                        matrices.add(new Matrix4f()
                            .translate(x,y,z)
                            .translate(0,0.5f,0)
                            .rotateX((float)Math.toRadians(-90))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.top.x,block.top.y));
                        lightLevels.add(new Vector4f(world.getBlockState(x,y+1,z).lightLevel));
                    }
                    if(world.getBlockState(x,y,z-1).block== Block.AIR){
                        //back
                        matrices.add(new Matrix4f()
                            .translate(x,y,z)
                            .translate(0,0,-0.5f)
                            .rotateY((float)Math.toRadians(180))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.back.x,block.back.y));
                        lightLevels.add(new Vector4f(world.getBlockState(x,y,z-1).lightLevel));
                    }
                    if(world.getBlockState(x,y,z+1).block== Block.AIR){
                        //front
                        matrices.add(new Matrix4f()
                            .translate(x,y,z)
                            .translate(0,0,0.5f)
                            .rotateY((float)Math.toRadians(0))
                        );
                        Block block=world.getBlockState(x,y,z).block;
                        uvs.add(new Vector2f(block.front.x,block.front.y));
                        lightLevels.add(new Vector4f(world.getBlockState(x,y,z+1).lightLevel));
                    }
                }
            }
        }
        int matrixSize=16;
        int uvSize=2;
        int lightLevelSize=4;
        int instanceSize=matrixSize+uvSize+lightLevelSize;
        float[] instanceData=new float[matrices.size()*instanceSize];
        float[] matrixData=new float[matrixSize];
        float[] data=new float[uvSize];
        for(int i=0;i<matrices.size();i++){
            matrices.get(i).get(matrixData);
            data[0]=uvs.get(i).x;
            data[1]=uvs.get(i).y;
            for(int j=0;j<matrixSize;j++){
                instanceData[i*instanceSize+j]=matrixData[j];
            }
            for(int j=0;j<uvSize;j++){
                instanceData[i*instanceSize+matrixSize+j]=data[j];
            }

        }
        instancesToDraw=matrices.size();
        mesh.setInstanceData(instanceData);
    }
}
