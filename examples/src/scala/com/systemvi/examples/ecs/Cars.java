package com.systemvi.examples.ecs;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.camera.CameraController3;
import com.systemvi.engine.model.Model;
import com.systemvi.engine.model.ModelLoaderParams;
import com.systemvi.engine.model.ModelUtils;
import com.systemvi.engine.renderers.PhongRenderer;
import com.systemvi.engine.ui.utils.data.Colors;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class Cars extends Game {

    public Cars(){
        super(3,3,60,800,600,"Cars");
    }
    public Dominion dominion;
    public Camera3 camera;
    public Model model;
    public PhongRenderer renderer;
    public ArrayList<Runnable> systems;
    public Entity player;
    @Override
    public void setup(Window window) {
        camera = Camera3.builder3d()
            .aspect((float)window.getWidth()/window.getHeight())
            .build();
        model = ModelUtils.load(ModelLoaderParams.builder()
                .fileName("assets/examples/models/cars/suv-luxury.glb")
                .triangulate()
                .calcTangentSpace()
                .genSmoothNormals()
                .fixInfacingNormals()
                .genSmoothNormals()
                .flipUVs()
                .joinIdenticalVertices()
            .build());
        model.root.transform.rotateY((float)Math.PI);
        renderer=new PhongRenderer(model, camera,"");
        dominion=Dominion.create();
        player=dominion.createEntity(
            camera,
            renderer,
            new Position(0,0,0),
            new Rotation(0,0,0),
            new Scale(1,1,1)
        );
        systems=new ArrayList<>();
        systems.add(()->{
            dominion.findEntitiesWith(
                Camera3.class,
                Position.class
            ).stream().forEach((entity)->{
                Camera3 camera=entity.comp1();
                Position position=entity.comp2();
                camera.position(position.x, position.y+1, position.z+5)
                    .update();
            });
        });
        systems.add(()->{
            dominion.findEntitiesWith(
                PhongRenderer.class,
                Position.class,
                Rotation.class,
                Scale.class
            ).stream().forEach((entity)->{
                PhongRenderer renderer=entity.comp1();
                Position position=entity.comp2();
                Rotation rotation=entity.comp3();
                Scale scale=entity.comp4();
                renderer.render(new Matrix4f()
                    .translate(position.x,position.y,position.z)
                    .rotateXYZ(rotation.x,rotation.y,rotation.z)
                    .scale(scale.x,scale.y,scale.z)
                );
            });
        });

    }

    @Override
    public void loop(float delta) {
        Utils.clear(Colors.black(), Utils.Buffer.COLOR_BUFFER, Utils.Buffer.DEPTH_BUFFER);
        for(Runnable r:systems){
            r.run();
        }
    }
    record Position(float x, float y,float z){}
    record Rotation(float x,float y,float z){}
    record Scale(float x,float y,float z){}
    record Velocity(float x,float y,float z){}
    record Acceleration(float x,float y,float z){}

}
