package com.systemvi.examples.minecraft.renderer;

import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.shader.Shader;
import com.systemvi.examples.minecraft.world.Chunk;
import com.systemvi.examples.test3d.CameraController;

public class BlockFaceRenderer {
    private Shader shader;
    public BlockFaceRenderer(){
        shader=Shader.builder()
            .fragment("assets/examples/minecraft/blockFaceRenderer/fragment.glsl")
            .vertex("assets/examples/minecraft/blockFaceRenderer/vertex.glsl")
            .build();
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
    }

    public void use(){
        shader.use();
    }

    public void setCamera(Camera camera){
        shader.setUniform("view",camera.getView());
        shader.setUniform("projection",camera.getProjection());
    }
    public void drawChunk(Chunk chunk){
        chunk.mesh.drawInstancedElements(chunk.triangles,chunk.instancesToDraw);
    }
}
