//package com.systemvi.voxel.world.world.cache;
//
//import com.systemvi.voxel.world.world2.Block;
//import com.systemvi.voxel.world.world2.BlockState;
//import com.systemvi.voxel.world.world.Chunk;
//import com.systemvi.voxel.world.world.World;
//import org.joml.Vector3i;
//
//public class ChunkCache {
//    public ChunkCache(){
//
//    }
//    public ChunkCache regenerate(World world, Vector3i chunkPosition){
//        for(int i=0;i< Chunk.SIZE_X;i++){
//            for(int j=0;j< Chunk.SIZE_Y;j++){
//                for(int k=0;k< Chunk.SIZE_Z;k++){
//                    Vector3i worldPosition=new Vector3i(chunkPosition).mul(Chunk.SIZE_X,Chunk.SIZE_Y,Chunk.SIZE_Z).add(i,j,k);
//                    BlockState state=world.getBlockState(worldPosition);
//                    Block block=state.block;
//                    if(block.opaque){
//                        //check left
//                        state=world.getBlockState(worldPosition.x-1,worldPosition.y,worldPosition.z);
//                        if(!state.block.opaque){
//                            //save rect transform,
//                            //add this blocks left side
//                        }
//                        //check right
//                        state=world.getBlockState(worldPosition.x+1,worldPosition.y,worldPosition.z);
//                        if(!state.block.opaque){
//                            //add this blocks right side
//                        }
//                        //check front
//                        state=world.getBlockState(worldPosition.x,worldPosition.y,worldPosition.z+1);
//                        if(!state.block.opaque){
//                            //add this blocks front side
//                        }
//                        //check back
//                        state=world.getBlockState(worldPosition.x,worldPosition.y,worldPosition.z-1);
//                        if(!state.block.opaque){
//                            //add this blocks back side
//                        }
//                        //check top
//                        state=world.getBlockState(worldPosition.x,worldPosition.y+1,worldPosition.z);
//                        if(!state.block.opaque){
//                            //add this blocks top side
//                        }
//                        //check bottom
//                        state=world.getBlockState(worldPosition.x,worldPosition.y-1,worldPosition.z);
//                        if(!state.block.opaque){
//                            //add this blocks bottom side
//                        }
//                    }
//                }
//            }
//        }
//        return this;
//    }
//}
