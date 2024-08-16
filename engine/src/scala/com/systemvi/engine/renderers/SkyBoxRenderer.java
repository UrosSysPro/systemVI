package com.systemvi.engine.renderers;

import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.CubeMap;

public class SkyBoxRenderer {
    private CubeMap cubeMap;
    private Mesh mesh;
    private Shader shader;

    public SkyBoxRenderer(CubeMap cubeMap) {
        this.cubeMap = cubeMap;
        mesh = new Mesh(new VertexAttribute("position", 3));
        mesh.setVertexData(new float[]{
            0, 0, 0,
            0, 0, 1,
            0, 1, 0,
            0, 1, 1,
            1, 0, 0,
            1, 0, 1,
            1, 1, 0,
            1, 1, 1,
        });
        mesh.setIndices(new int[]{
            //bottom
            0, 1, 5,
            0, 4, 5,
            //top
            2, 3, 7,
            2, 6, 7,
            //right
            4, 5, 7,
            4, 7, 6,
            //left
            0, 1, 2,
            1, 2, 3,
            //front
            1,5,3,
            5,3,7,
            //back
            0,4,6,
            0,6,2,
        });
        shader=Shader.builder()
            .fragment("assets/renderer/cubeMapRenderer/fragment.glsl")
            .vertex("assets/renderer/cubeMapRenderer/vertex.glsl")
            .build();
        if(!shader.isCompiled()){
            System.out.printf(shader.getLog());
        }
    }

    public void draw(Camera camera){
        shader.use();
        cubeMap.bind();
        shader.setUniform("cubeMap",0);
        shader.setUniform("view",camera.getView());
        shader.setUniform("projection",camera.getProjection());
        mesh.drawElements(12);
    }
}
