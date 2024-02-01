package com.systemvi.voxel.world.renderer;

import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.FrameBuffer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.Utils;
import com.systemvi.voxel.world.materials.Material;
import com.systemvi.voxel.world.world.Chunk;
import com.systemvi.voxel.world.world.World;

public class WorldRenderer {
    public Texture uv,normal,depth,position;
    private final FrameBuffer frameBuffer;
    private final Shader blockFaceShader;
    public WorldRenderer(int width,int height){
        uv=new Texture(width,height, Format.RG);
        depth=new Texture(width,height,Format.DEPTH32);
        normal=new Texture(width,height,Format.RGB);
        position=new Texture(width,height,Format.RGB32);

        frameBuffer= FrameBuffer.builder()
            .color(uv)
            .color(normal)
            .color(position)
            .depth(depth)
            .build();

        blockFaceShader=Shader.builder()
            .fragment("assets/examples/minecraft/blockFaceRenderer/fragment.glsl")
            .vertex("assets/examples/minecraft/blockFaceRenderer/vertex.glsl")
            .build();
    }

    public void render(World world, Camera camera, Material material){
        Chunk[][][] chunks=world.getChunks();

        frameBuffer.begin();
        Utils.clear(0,0,0,0, Utils.Buffer.COLOR_BUFFER, Utils.Buffer.DEPTH_BUFFER);
        Utils.enableDepthTest();
        Utils.enableFaceCulling();

        blockFaceShader.use();
        blockFaceShader.setUniform("view",camera.getView());
        blockFaceShader.setUniform("projection",camera.getProjection());

        material.normal.bind(0);
        blockFaceShader.setUniform("normalMap",0);

        for(int i=0;i<chunks.length;i++){
            for(int j=0;j<chunks[i].length;j++){
                for(int k=0;k<chunks[i][j].length;k++){
                    Chunk chunk=chunks[i][j][k];
                    chunk.mesh.drawInstancedElements(chunk.triangles,chunk.instancesToDraw);
                }
            }
        }

        Utils.disableDepthTest();
        Utils.disableFaceCulling();
        frameBuffer.end();
    }
}
