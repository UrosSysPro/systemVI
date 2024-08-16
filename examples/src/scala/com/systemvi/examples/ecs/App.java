package com.systemvi.examples.ecs;

import com.systemvi.engine.application.Game;
import com.systemvi.engine.camera.Camera3;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.ui.utils.data.Colors;
import com.systemvi.engine.utils.Utils;
import com.systemvi.engine.window.Window;
import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Scheduler;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class App extends Game {
    public App(){
        super(3,3,60,800,600,"Ecs");
    }
    public ShapeRenderer renderer;
    public Camera3 camera;
    public Dominion dominion;
    public Runnable speedSystem,drawSystem;
    @Override
    public void setup(Window window) {
        dominion=Dominion.create();
        dominion.createEntity(
            "o1",
            new Position(100,100),
            new Velocity(1,1),
            new Drawable()
        );
        dominion.createEntity(
            new Position(100,100),
            new Velocity(1,-1),
            new Drawable()
        );
        speedSystem = () -> {
             dominion.findEntitiesWith(Position.class, Velocity.class)
                 .stream().forEach(result -> {
                     Position position = result.comp1();
                     Velocity velocity = result.comp2();
                     position.x += velocity.x;
                     position.y += velocity.y;
                 });
        };
        speedSystem=new Runnable() {
             @Override
             public void run() {
                 dominion.findEntitiesWith(Position.class, Velocity.class)
                     .stream().forEach(result -> {
                         Position position = result.comp1();
                         Velocity velocity = result.comp2();
                         position.x += velocity.x;
                         position.y += velocity.y;
                     });
             }
         };
         drawSystem = () -> {
            dominion.findEntitiesWith(Position.class, Drawable.class)
                .stream().forEach(result -> {
                    Position position = result.comp1();
                    renderer.rect(position.x,position.y,100,100,Colors.green500());
                    renderer.flush();
                });
        };

        camera=Camera3.builder2d()
            .size(window.getWidth(),window.getHeight())
            .position(window.getWidth()/2,window.getHeight()/2)
            .build();
        renderer=new ShapeRenderer();
        renderer.setView(camera.view());
        renderer.setProjection(camera.projection());
    }

    @Override
    public void loop(float delta) {
        Utils.clear(new Vector4f(0), Utils.Buffer.COLOR_BUFFER);
        speedSystem.run();
        drawSystem.run();
    }


    static class Position{
        public float x,y;
        public Position(float x, float y){
            this.x=x;
            this.y=y;
        }
    }
    static class Velocity{
        public float x,y;
        public Velocity(float x, float y){
            this.x=x;
            this.y=y;
        }
    }
    static class Drawable{

    }
}
