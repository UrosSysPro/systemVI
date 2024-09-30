package com.systemvi.normal_mapping;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.camera.CameraController3;
import com.systemvi.engine.model.Mesh;
import com.systemvi.engine.model.VertexAttribute;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL33.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL33.glEnable;

public class App extends Game {

    public App() {
        super(3,3,60,800,600,"Normal Mapping");
    }
    public Mesh mesh;
    public Shader shader;
    public Camera3 camera;
    public CameraController3 controller;
    public Texture diffuse,specular,ambient,normal;
    public float angle;

    @Override
    public void setup(Window window) {
        glfwWindowHint(GLFW_SAMPLES,3);
        mesh=new Mesh(
            new VertexAttribute("position",3),
            new VertexAttribute("normal", 3),
            new VertexAttribute("tangent", 3),
            new VertexAttribute("bitangent", 3),
            new VertexAttribute("uv",2)
        );
        float size=1;
        mesh.setVertexData(new float[]{
            //position            normal  tangent  bitangent   uv
            -size,  size, 0,      0,0,1,  1,0,0,   0,1,0,      0,1,
             size,  size, 0,      0,0,1,  1,0,0,   0,1,0,      1,1,
            -size, -size, 0,      0,0,1,  1,0,0,   0,1,0,      0,0,
             size, -size, 0,      0,0,1,  1,0,0,   0,1,0,      0,1,
        });
        mesh.setIndices(new int[]{
            0,1,2,
            1,3,2,
        });
        controller=CameraController3.builder()
            .camera(Camera3.builder3d()
                .position(0,0,10)
                .build())
            .window(window)
            .speed(5)
            .aspect((float)window.getWidth()/window.getHeight())
            .build();
        camera=controller.camera();
        setInputProcessor(controller);

        shader=Shader.builder()
            .fragment("fragment.glsl")
            .vertex("vertex.glsl")
            .build();

        if(!shader.isCompiled()){
            System.out.println(shader.getLog());
        }
        angle=0;

        diffuse=new Texture("diffuse.png");
        specular=new Texture("roughness.png");
        ambient=new Texture("ambientOclusion.png");
        normal=new Texture("normal.png");

    }

    @Override
    public void loop(float delta) {

        Utils.clear(0,0,0,1, Utils.Buffer.COLOR_BUFFER, Utils.Buffer.DEPTH_BUFFER);

        controller.update(delta);
        angle+=0.01;

        Vector3f lightPosition=new Vector3f(2,2,2);

        Utils.enableDepthTest();
        Utils.enableFaceCulling(Utils.Face.FRONT);
        glEnable(GL_MULTISAMPLE);
        shader.use();
        shader.setUniform("view",camera.view());
        shader.setUniform("projection",camera.projection());

        diffuse.bind(0);
        specular.bind(1);
        ambient.bind(2);
        normal.bind(3);

        shader.setUniform("diffuseTexture",0);
        shader.setUniform("specularTexture",1);
        shader.setUniform("ambientTexture",2);
        shader.setUniform("normalTexture",3);

        shader.setUniform("lightPosition",lightPosition);
        shader.setUniform("lightColor",new Vector3f(1,1,1));
        shader.setUniform("cameraPosition",camera.position());

        drawCube(new Matrix4f().identity().translate(3,0,0).rotateXYZ(angle,angle,angle));
        drawCube(new Matrix4f().identity().translate(0,0,0));
        drawCube(new Matrix4f().identity().translate(-3,0,0).scale((float)Math.sin(angle)));
//        drawCube(lightPosition.x,lightPosition.y,lightPosition.z);
        Utils.disableFaceCulling();
        Utils.disableDepthTest();
    }

    public void drawCube(float x,float y,float z) {
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
    public void drawCube(Matrix4f transform) {
        //prednja strana
        shader.setUniform("model", new Matrix4f()
            .identity()
            .mul(transform)
            .translate(0, 0, 1));
        shader.setUniform("color", new Vector4f(0.3f, 0.6f, 0.9f, 1.0f));
        mesh.drawElements(2);
        //zadnja
        shader.setUniform("model", new Matrix4f()
            .identity()
            .mul(transform)
            .translate(0, 0, -1)
            .rotateY((float)Math.toRadians(180))
        );
        shader.setUniform("color", new Vector4f(0.7f, 0.6f, 0.5f, 1.0f));
        mesh.drawElements(2);
        //desno
        shader.setUniform("model", new Matrix4f()
            .identity()
            .mul(transform)
            .translate(1, 0, 0)
            .rotateY((float) Math.toRadians(90))
        );
        shader.setUniform("color", new Vector4f(0.4f, 0.3f, 0.8f, 1.0f));
        mesh.drawElements(2);
        //leva strana
        shader.setUniform("model", new Matrix4f()
            .identity()
            .mul(transform)
            .translate(-1, 0, 0)
            .rotateY((float) Math.toRadians(-90))
        );
        shader.setUniform("color", new Vector4f(0.3f, 0.2f, 0.7f, 1.0f));
        mesh.drawElements(2);
        //gornja strana
        shader.setUniform("model", new Matrix4f()
            .identity()
            .mul(transform)
            .translate(0, 1, 0)
            .rotateX((float) Math.toRadians(-90))
        );
        shader.setUniform("color", new Vector4f(0.1f, 0.8f, 0.2f, 1.0f));
        mesh.drawElements(2);
        shader.setUniform("model", new Matrix4f()
            .identity()
            .mul(transform)
            .translate(0, -1, 0)
            .rotateX((float) Math.toRadians(90))
        );
        shader.setUniform("color", new Vector4f(0.32f, 0.8768f, 0.432f, 1.0f));
        mesh.drawElements(2);
    }
}
