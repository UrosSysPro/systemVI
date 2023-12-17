package com.systemvi.examples.minecraft.renderer;

import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.shader.Shader;
import com.systemvi.examples.minecraft.world.Chunk;

public class BlockFaceRenderer {
    private Shader shader;
    public BlockFaceRenderer(){
        shader=Shader.builder()
            .fragment("")
            .vertex("")
            .build();
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
    }

    public void use(){
        shader.use();
    }
    public void drawChunk(Chunk chunk){

    }
}
