package com.systemvi.examples.physics2d;

import com.systemvi.engine.application.Application;
import com.systemvi.engine.camera.Camera;
import com.systemvi.engine.renderers.ShapeRenderer;
import com.systemvi.engine.window.Window;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL33.*;

public class Physics extends Application {
    public Physics(int openglVersionMajor, int openglVersionMinor, int targetFPS) {
        super(openglVersionMajor, openglVersionMinor, targetFPS);
    }

    Window window;
    World world;
    ShapeRenderer renderer;
    Camera camera;
    float x,y;
    ArrayList<Body> bodies;

    @Override
    public void setup() {
        window=new Window(800,600,"Box2d demo");
        world=new World(new Vec2(0,600));
        renderer=new ShapeRenderer();
        camera=new Camera();
        camera.setPosition(400,300,0);
        camera.setScreenSize(800,600);
        camera.setScale(1,-1,1);
        camera.update();
        renderer.setCamera(camera);
        window.addOnMouseMoveListener((x1, y1) -> {
            x=(int)x1;
            y=(int)y1;
        });
        bodies=new ArrayList<>();

        window.addOnMouseDownListener((button, mods) -> {
            BodyDef bodyDef=new BodyDef();
            bodyDef.position.set(x,y);
            bodyDef.type= BodyType.DYNAMIC;
            bodyDef.linearDamping=0;
            FixtureDef fixtureDef=new FixtureDef();
            fixtureDef.density=1;
            fixtureDef.restitution=0.5f;
            fixtureDef.friction=0.7f;
            PolygonShape shape=new PolygonShape();
            shape.setAsBox(20f,20f);

            fixtureDef.shape=shape;

            Body body=world.createBody(bodyDef);
            body.createFixture(fixtureDef);
            bodies.add(body);
        });
        createWalls();
    }

    @Override
    public void loop(float delta) {
        if(window.shouldClose())close();
        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);
        //input
        window.pollEvents();
        //update
        world.step(delta,10,10);
        //draw
//        renderer.rect();
        for(int i=0;i<bodies.size();i++){
            Body body=bodies.get(i);
            float x=body.getPosition().x;
            float y=body.getPosition().y;
            float a=body.getAngle();
            renderer.rect(x-20,y-20,40,40,new Vector4f(1),a);
        }
        renderer.flush();

        window.swapBuffers();
    }

    public void createWalls(){
        BodyDef bodyDef=new BodyDef();
        bodyDef.position.set(400f,560f);
        bodyDef.type= BodyType.STATIC;

        FixtureDef fixtureDef=new FixtureDef();
        fixtureDef.density=1;
        fixtureDef.restitution=0.5f;
        fixtureDef.friction=0.7f;
        PolygonShape shape=new PolygonShape();
        shape.setAsBox(400f,21f);

        fixtureDef.shape=shape;

        Body body=world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        bodies.add(body);
    }
}
