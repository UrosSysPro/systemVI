package com.systemvi.physics2d;

import com.systemvi.engine.application.{Application, Game}
import com.systemvi.engine.camera.{Camera, Camera3}
import com.systemvi.engine.physics2d.PhysicsBody
import com.systemvi.engine.renderers.ShapeRenderer
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*
import org.joml.{Vector2f, Vector4f}

import java.util.ArrayList
import org.lwjgl.opengl.{GL11, GL33}


object Physics extends Game(3,3,60,800,600,"Physics") {

    var world:World=null
    var renderer:ShapeRenderer=null
    var camera:Camera3=null
    var mouse=new Vector2f()
    var bodies:Array[Body]=Array()

    override def setup(window:Window):Unit = {
        val width=window.getWidth.toFloat
        val height=window.getHeight.toFloat
        world=new World(new Vec2(0,600))
        renderer=new ShapeRenderer()
        camera=Camera3.builder2d()
          .position(width/2,height/2)
          .size(width, height)
          .build()

        camera.update()
        renderer.setView(camera.view)
        renderer.setProjection(camera.projection)

        createWalls()
    }

    override def loop(delta:Float):Unit = {
        val window=getWindow
        if(window.shouldClose())close()
        Utils.clear(Colors.black,Buffer.COLOR_BUFFER)
        //input
        window.pollEvents();
        //update
        world.step(delta,10,10);
        //draw
        for(body<-bodies){
            val x=body.getPosition().x
            val y=body.getPosition().y
            val a=body.getAngle()
            renderer.rect(x-20,y-20,40,40,new Vector4f(1),a)
        }
        renderer.flush()
    }

    def createWalls():Unit = {
        val bodyDef=new BodyDef();
        bodyDef.position.set(400f,560f)
        bodyDef.`type` = BodyType.STATIC

        val fixtureDef=new FixtureDef()
        fixtureDef.density=1
        fixtureDef.restitution=0.5f
        fixtureDef.friction=0.7f
        val shape=new PolygonShape()
        shape.setAsBox(400f,21f)

        fixtureDef.shape=shape

        val body=world.createBody(bodyDef)
        body.createFixture(fixtureDef)
        bodies=bodies ++ Array(body)
    }

    override def mouseDown(button: Int, mods: Int, x: Double, y: Double): Boolean = {
        val bodyDef = new BodyDef()
        bodyDef.position.set(mouse.x, mouse.y)
        bodyDef.`type` = BodyType.DYNAMIC
        bodyDef.linearDamping = 0
        val fixtureDef = new FixtureDef()
        fixtureDef.density = 1
        fixtureDef.restitution = 0.5f
        fixtureDef.friction = 0.7f
        val shape = new PolygonShape()
        shape.setAsBox(20f, 20f)

        fixtureDef.shape = shape;

        val body = world.createBody(bodyDef)
        body.createFixture(fixtureDef)
        bodies = bodies ++ Array(body)
        true
    }

    override def resize(width: Int, height: Int): Boolean = {
        camera.position.set(width/2f,height/2f,0)
        camera.orthographic(-width/2f,width/2f,-height/2f,height/2f,0,1)
        camera.update()
        true
    }
    override def mouseMove(x: Double, y: Double): Boolean = {
        mouse.set(x,y)
        true
    }
}
