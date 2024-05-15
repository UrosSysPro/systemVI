package com.systemvi.examples.test3d.modelloading;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.camera.CameraController3;
import com.systemvi.engine.model.Model;
import com.systemvi.engine.model.ModelLoader;
import com.systemvi.engine.model.ModelLoaderParams;
import com.systemvi.engine.shader.Primitive;
import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;

public class App extends Game {
    public App(){
        super(3,3,60,800,600,"Model Loading");
    }

    public Model model;
    public CameraController3 controller;
    public Shader shader;

    @Override
    public void setup(Window window) {
        model = ModelLoader.load(
            ModelLoaderParams.builder()
                .fileName("assets/examples/models/castle/tower.glb")
                .triangulate()
                .fixInfacingNormals()
                .joinIdenticalVertices()
                .build()
        );
        controller=CameraController3.builder()
            .camera(Camera3.builder3d()
                .build())
            .speed(10)
            .window(window)
            .build();
        setInputProcessor(controller);
        shader= Shader.builder()
            .vertex("assets/models/vertex.glsl")
            .fragment("assets/models/fragment.glsl")
            .build();
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0,0,0,1, Utils.Buffer.COLOR_BUFFER);
        controller.update(delta);
        Model.Mesh mesh=model.meshes.get(0);
        mesh.bind();
        shader.use();
        shader.setUniform("view",controller.camera().view());
        shader.setUniform("projection",controller.camera().projection());
        shader.drawArrays(Primitive.TRIANGLES,mesh.vertices.size());
    }
}
