package com.systemvi.voxel.world.world;

import com.systemvi.engine.buffer.ArrayBuffer;
import com.systemvi.engine.buffer.ElementsBuffer;
import com.systemvi.engine.buffer.VertexArray;
import com.systemvi.engine.model.VertexAttribute;
import org.joml.Vector3i;

public class ChunkCache2 {
    private final World world;
    private final Vector3i chunkId;
    private final VertexArray vertexArray;
    private final ArrayBuffer vertexBuffer;
    private final ArrayBuffer instanceBuffer;
    private final ElementsBuffer elementsBuffer;


    public ChunkCache2(World world, Vector3i chunkId){
        this.world=world;
        this.chunkId=chunkId;
        vertexArray=new VertexArray();
        vertexBuffer=new ArrayBuffer();
        vertexBuffer.setVertexAttributes(new VertexAttribute[]{
            new VertexAttribute("position",3),
            new VertexAttribute("tangent",3),
            new VertexAttribute("bitangent",3),
            new VertexAttribute("normal",3),
            new VertexAttribute("texCoords",2),
        });
        vertexBuffer.setData(new float[]{

        });

        elementsBuffer=new ElementsBuffer();
        elementsBuffer.setData(new int[]{
            0,1,2,
            0,2,3
        });

        instanceBuffer=new ArrayBuffer();
        instanceBuffer.setVertexAttributes(0, new VertexAttribute[]{

        });
        instanceBuffer.setVertexAttributeDivisor(0,2);
        instanceBuffer.setData(new float[]{});
    }
}
