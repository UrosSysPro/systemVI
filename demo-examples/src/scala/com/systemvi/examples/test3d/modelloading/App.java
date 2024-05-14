package com.systemvi.examples.test3d.modelloading;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.model.Model;
import com.systemvi.engine.model.ModelLoader;
import com.systemvi.engine.model.ModelLoaderParams;
import com.systemvi.engine.window.Window;

public class App extends Game {
    public App(){
        super(3,3,60,800,600,"Model Loading");
    }

    public Model model;

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
        System.out.println(model.meshes.get(0).materialIndex);
        if(model==null){
            System.out.println("[Error] error loading model");
        }else{
            System.out.println("[SUCCESS] model loaded");
        }
    }

    @Override
    public void loop(float delta) {

    }
}
