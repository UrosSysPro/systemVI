package com.systemvi.engine.physics2d;

import com.systemvi.engine.renderers.ShapeRenderer;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;
import org.joml.Vector4f;

public class Rect extends PhysicsBody{
    private float width,height;
    public Rect(World world,float x, float y, float width, float height){
        BodyDef bodyDef=new BodyDef();
        bodyDef.position.set(x,y);
        bodyDef.type= BodyType.DYNAMIC;
        bodyDef.linearDamping=0;
        FixtureDef fixtureDef=new FixtureDef();
        fixtureDef.density=1;
        fixtureDef.restitution=0.5f;
        fixtureDef.friction=0.7f;
        PolygonShape shape=new PolygonShape();
        this.width=width;
        this.height=height;
        shape.setAsBox(width,height);

        fixtureDef.shape=shape;

        Body body=world.createBody(bodyDef);
        body.createFixture(fixtureDef);
    }

    @Override
    public void debugDraw(ShapeRenderer renderer) {
        float x=getPosition().x;
        float y=getPosition().y;
        renderer.rect(x-width,y-height,width,height,new Vector4f(1),getAngle());
    }
}
