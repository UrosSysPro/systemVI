package com.systemvi.examples.minecraft.world;

import com.systemvi.engine.texture.TextureRegion;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class BlockFace {

    public static Vector3f[] UP=new Vector3f[]{
      new Vector3f(-0.5f, 0.5f, 0.5f),
      new Vector3f(-0.5f, 0.5f,-0.5f),
      new Vector3f( 0.5f, 0.5f,-0.5f),
      new Vector3f( 0.5f, 0.5f, 0.5f)
    },DOWN=new Vector3f[]{
        new Vector3f(-0.5f,-0.5f, 0.5f),
        new Vector3f(-0.5f,-0.5f,-0.5f),
        new Vector3f( 0.5f,-0.5f,-0.5f),
        new Vector3f( 0.5f,-0.5f, 0.5f)
    },LEFT=new Vector3f[]{
        new Vector3f(-0.5f, 0.5f, 0.5f),
        new Vector3f(-0.5f, 0.5f,-0.5f),
        new Vector3f(-0.5f, -0.5f, 0.5f),
        new Vector3f(-0.5f, -0.5f, -0.5f),
    },RIGHT=new Vector3f[]{
        new Vector3f( 0.5f, 0.5f, 0.5f),
        new Vector3f( 0.5f, 0.5f,-0.5f),
        new Vector3f( 0.5f, -0.5f, 0.5f),
        new Vector3f( 0.5f, -0.5f, -0.5f),
    },FRONT=new Vector3f[]{
        new Vector3f(0.5f,0.5f,0.5f),
        new Vector3f(0.5f,-0.5f,0.5f),
        new Vector3f(-0.5f,0.5f,0.5f),
        new Vector3f(-0.5f,-0.5f,0.5f),
    },BACK=new Vector3f[]{
        new Vector3f(0.5f,0.5f,-0.5f),
        new Vector3f(0.5f,-0.5f,-0.5f),
        new Vector3f(-0.5f,0.5f,-0.5f),
        new Vector3f(-0.5f,-0.5f,-0.5f),
    };

    public Vector3i position;
    public TextureRegion region;
    public Vector3f[] points;

    public BlockFace(Vector3i position,TextureRegion region,Vector3f[] points){
        this.points=points;
        this.position=position;
        this.region=region;
    }
}
