package com.systemvi.voxel.world.world;

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
    public Chunk(Vector3i chunkPosition, Perlin2d noise) {
        blockStates = new BlockState[SIZE_X][SIZE_Y][SIZE_Z];
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
    }
}
