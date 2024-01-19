package com.systemvi.examples.minecraft.renderer;

import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.renderers.TextureRenderer;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.FrameBuffer;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.OpenGLUtils;
import com.systemvi.examples.minecraft.world.Chunk;
import com.systemvi.examples.minecraft.world.World;

public class WorldRenderer {
    private BlockFaceRenderer blockFaceRenderer;
    public Texture color,normal,depth;
    private FrameBuffer frameBuffer;
    private TextureRenderer renderer;
    private Shader shader;
    public WorldRenderer(int width,int height){
        blockFaceRenderer=new BlockFaceRenderer();
        color=new Texture(width,height, Format.RGB);

        depth=Texture.depth(width,height,Format.DEPTH);
        normal=new Texture(width,height,Format.RGB);

        frameBuffer= FrameBuffer.builder()
            .color(color)
            .color(normal)
            .depth(depth)
            .build();
    }

    public void render(World world,Camera camera){
        Chunk[][][] chunks=world.getChunks();

        frameBuffer.begin();
        OpenGLUtils.clear(0,0,0,0, OpenGLUtils.Buffer.COLOR_BUFFER, OpenGLUtils.Buffer.DEPTH_BUFFER);
        OpenGLUtils.enableDepthTest();
        OpenGLUtils.enableFaceCulling();

        blockFaceRenderer.use();
        blockFaceRenderer.setCamera(camera);
        for(int i=0;i<chunks.length;i++){
            for(int j=0;j<chunks[i].length;j++){
                for(int k=0;k<chunks[i][j].length;k++){
                    blockFaceRenderer.drawChunk(chunks[i][j][k]);
                }
            }
        }

        OpenGLUtils.disableDepthTest();
        OpenGLUtils.disableFaceCulling();
        frameBuffer.end();
    }
}
