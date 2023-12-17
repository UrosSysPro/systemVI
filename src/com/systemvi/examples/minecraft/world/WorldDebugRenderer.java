package com.systemvi.examples.minecraft.world;

import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Texture;
import com.systemvi.examples.test3d.CameraController;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldDebugRenderer {
    public Mesh mesh;
    public Shader shader;
    public Texture diffuse,specular,ambient;

    public WorldDebugRenderer(){
        mesh=new Mesh(
            new VertexAttribute("position",3),
            new VertexAttribute("normal", 3),
            new VertexAttribute("uv",2)
        );
        float size=0.5f;
        mesh.setVertexData(new float[]{
            //position      normal      uv
            -size,  size, 0,      0,0,1,      0,1,
            size,  size, 0,      0,0,1,      1,1,
            -size, -size, 0,      0,0,1,      0,0,
            size, -size, 0,      0,0,1,      0,1,
        });
        mesh.setIndices(new int[]{
            0,1,2,
            1,3,2,
        });
        shader=new Shader(
            "assets/examples/test3d/vertex.glsl",
            "assets/examples/test3d/fragment.glsl"
        );
        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
        diffuse=new Texture("assets/examples/test3d/rock/diffuse.png");
        specular=new Texture("assets/examples/test3d/rock/roughness.png");
        ambient=new Texture("assets/examples/test3d/rock/ambientOclusion.png");
    }

    public void use(CameraController controller){
        shader.use();
        shader.setUniform("view",controller.camera.getView());
        shader.setUniform("projection",controller.camera.getProjection());

        diffuse.bind(0);
        specular.bind(1);
        ambient.bind(2);

        shader.setUniform("diffuseTexture",0);
        shader.setUniform("specularTexture",1);
        shader.setUniform("ambientTexture",2);

        shader.setUniform("lightPosition",new Vector3f(100,100,100));
        shader.setUniform("lightColor",new Vector3f(1,1,1));
        shader.setUniform("cameraPosition",new Vector3f(controller.x,controller.y,controller.z));

    }
    public void draw(float x,float y,float z){
//prednja strana
        shader.setUniform("model", new Matrix4f()
            .identity()
            .translate(x,y,z)
            .translate(0, 0, 1));
        shader.setUniform("color", new Vector4f(0.3f, 0.6f, 0.9f, 1.0f));
        mesh.drawElements(2);
        //zadnja
        shader.setUniform("model", new Matrix4f()
            .identity()
            .translate(x,y,z)
            .translate(0, 0, -1)
            .rotateY((float)Math.toRadians(180))
        );
        shader.setUniform("color", new Vector4f(0.7f, 0.6f, 0.5f, 1.0f));
        mesh.drawElements(2);
        //desno
        shader.setUniform("model", new Matrix4f()
            .identity()
            .translate(x,y,z)
            .translate(1, 0, 0)
            .rotateY((float) Math.toRadians(90))
        );
        shader.setUniform("color", new Vector4f(0.4f, 0.3f, 0.8f, 1.0f));
        mesh.drawElements(2);
        //leva strana
        shader.setUniform("model", new Matrix4f()
            .identity()
            .translate(x,y,z)
            .translate(-1, 0, 0)
            .rotateY((float) Math.toRadians(-90))
        );
        shader.setUniform("color", new Vector4f(0.3f, 0.2f, 0.7f, 1.0f));
        mesh.drawElements(2);
        //gornja strana
        shader.setUniform("model", new Matrix4f()
            .identity()
            .translate(x,y,z)
            .translate(0, 1, 0)
            .rotateX((float) Math.toRadians(-90))
        );
        shader.setUniform("color", new Vector4f(0.1f, 0.8f, 0.2f, 1.0f));
        mesh.drawElements(2);
        shader.setUniform("model", new Matrix4f()
            .identity()
            .translate(x,y,z)
            .translate(0, -1, 0)
            .rotateX((float) Math.toRadians(90))
        );
        shader.setUniform("color", new Vector4f(0.32f, 0.8768f, 0.432f, 1.0f));
        mesh.drawElements(2);
    }
}
