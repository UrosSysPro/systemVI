//package com.systemvi.voxel.world.renderer;
//
//import com.systemvi.engine.camera.Camera3;
//import com.systemvi.engine.shader.Shader;
//import com.systemvi.engine.texture.Format;
//import com.systemvi.engine.texture.FrameBuffer;
//import com.systemvi.engine.texture.Texture;
//import com.systemvi.engine.utils.Utils;
//import com.systemvi.voxel.world.materials.Material;
//import com.systemvi.voxel.world.world.Chunk;
//import com.systemvi.voxel.world.world.ChunkCache;
//import com.systemvi.voxel.world.world.World;
//
//public class WorldRenderer {
//    public Texture uv,normal,depth,position;
//    private final FrameBuffer frameBuffer;
//    private final Shader blockFaceShader;
//    public WorldRenderer(int width,int height){
//        uv=new Texture(width,height, Format.RG16);
//        depth=new Texture(width,height,Format.DEPTH16);
//        normal=new Texture(width,height,Format.RGB);
//        position=new Texture(width,height,Format.RGB32F);
//
//        frameBuffer= FrameBuffer.builder()
//            .color(uv)
//            .color(normal)
//            .color(position)
//            .depth(depth)
//            .build();
//
//        blockFaceShader=Shader.builder()
//            .fragment("assets/examples/minecraft/blockFaceRenderer/fragment.glsl")
//            .vertex("assets/examples/minecraft/blockFaceRenderer/vertex.glsl")
//            .build();
//    }
//
//    public void render(World world, Camera3 camera, Material material){
//        frameBuffer.begin();
//        Utils.clear(0,0,0,0, Utils.Buffer.COLOR_BUFFER, Utils.Buffer.DEPTH_BUFFER);
//        Utils.enableDepthTest();
//        Utils.enableFaceCulling();
//
//        blockFaceShader.use();
//        blockFaceShader.setUniform("view",camera.view());
//        blockFaceShader.setUniform("projection",camera.projection());
//
//        material.normal.bind(0);
//        blockFaceShader.setUniform("normalMap",0);
//
//        ChunkCache[][][] caches=world.getCaches();
//
//        for(ChunkCache[][] i:caches){
//            for(ChunkCache[] j:i){
//                for(ChunkCache k:j){
//                    k.mesh.drawInstancedElements(k.triangles,k.instancesToDraw);
//                }
//            }
//        }
//
//        Utils.disableDepthTest();
//        Utils.disableFaceCulling();
//        frameBuffer.end();
//    }
//}
