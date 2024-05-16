package com.systemvi.examples.test3d.modelloading;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.camera.CameraController3;
import com.systemvi.engine.model.Model;
import com.systemvi.engine.model.ModelUtils;
import com.systemvi.engine.model.ModelLoaderParams;
import com.systemvi.engine.renderers.PhongRenderer;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class App extends Game {
    public App(){
        super(3,3,60,800,600,"Model Loading");
    }

    public Model model;
    public CameraController3 controller;
    public PhongRenderer renderer;

    @Override
    public void setup(Window window) {
        model = ModelUtils.load(
            ModelLoaderParams.builder()
                .fileName("assets/examples/models/castle/tower.glb")
                .triangulate()
                .fixInfacingNormals()
                .genSmoothNormals()
                .joinIdenticalVertices()
                .build()
        );
        model.root.children.add(
            new Model.Node("new node",
                new ArrayList<>(),
                new ArrayList<>(){{add(0);}},
                new ArrayList<>(){{add(model.meshes.get(0));}},
                new Matrix4f().translate(1,1,1).scale(0.3f)
            )
        );
        model.root.transform.rotateXYZ(0.3f,0,0.4f);
        System.out.println("meshes: "+model.meshes.get(0).vertices.get(0).texCoords.size());
        controller=CameraController3.builder()
            .camera(Camera3.builder3d()
                .build())
            .speed(3)
            .aspect((float) window.getWidth() /window.getHeight())
            .window(window)
            .build();
        setInputProcessor(controller);
        renderer=new PhongRenderer(model);
    }

    @Override
    public void loop(float delta) {
        Utils.clear(0,0,0,1, Utils.Buffer.COLOR_BUFFER, Utils.Buffer.DEPTH_BUFFER);
        controller.update(delta);
        renderer.render(controller.camera(),new Matrix4f().translate(2,0,0));
        renderer.render(controller.camera(),new Matrix4f().translate(-2,0,0));
        renderer.render(controller.camera(),new Matrix4f().translate(0,0,0));
    }
}
