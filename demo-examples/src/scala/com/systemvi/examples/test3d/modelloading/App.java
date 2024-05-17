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
                .fileName("assets/examples/models/cars/sedan-sports.glb")
                .triangulate()
//                .fixInfacingNormals()
//                .genSmoothNormals()
//                .joinIdenticalVertices()
                .build()
        );
//        model.root.children.get(0).children.get(0).meshIndices.set(0,3);
        controller=CameraController3.builder()
            .camera(Camera3.builder3d()
                .build())
            .speed(3)
            .aspect((float) window.getWidth() /window.getHeight())
            .window(window)
            .build();
        setInputProcessor(controller);
        renderer=new PhongRenderer(model);
        System.out.println(model.meshes.size());
        print(model.root,"");
        System.out.println("meshes: "+model.meshes.size());
        System.out.println("materials: "+model.materials.size());
    }

    public void print(Model.Node node,String prefix){
        System.out.print(prefix+node.name+" "+node.children.size()+" ");
        for(int i=0;i<node.meshIndices.size();i++){
            System.out.print(node.meshIndices.get(i));
        }
        System.out.println();
        for(int i=0;i<node.children.size();i++){
            print(node.children.get(i),prefix+"\t");
        }
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
