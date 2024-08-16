package com.systemvi.engine.physics2d;

import com.systemvi.engine.renderers.ShapeRenderer;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public abstract class PhysicsBody {
    private Body body;

    public Body getBody() {
        return body;
    }
    public Vec2 getPosition(){
        return body.getPosition();
    }
    public Vec2 getVelocity(){
        return body.getLinearVelocity();
    }
    public float getAngle(){
        return body.getAngle();
    }
    public abstract void debugDraw(ShapeRenderer renderer);
}
