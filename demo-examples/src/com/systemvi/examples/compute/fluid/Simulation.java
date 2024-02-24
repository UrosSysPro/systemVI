package com.systemvi.examples.compute.fluid;

import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class Simulation {
    public Texture density, density_prev, u, u_prev, v, v_prev,helper;
    public Shader project, advect,advectX,advectY,fill;
    public int width, height;

    public Simulation(int width, int height){
        this.width = width;
        this.height = height;

        density         = new Texture(width, height, Format.R16F);
        density_prev    = new Texture(width, height, Format.R16F);
        u               = new Texture(width, height, Format.R16F);
        u_prev          = new Texture(width, height, Format.R16F);
        v               = new Texture(width, height, Format.R16F);
        v_prev          = new Texture(width, height, Format.R16F);
        helper          = new Texture(width, height, Format.R16F);

        project=Shader.builder().compute("assets/examples/compute/fluid/project.glsl").build();
        if(!project.isCompiled()) System.out.println(project.getLog());

        advect=Shader.builder().compute("assets/examples/compute/fluid/advect.glsl").build();
        if(!advect.isCompiled())System.out.println(advect.getLog());

        advectX=Shader.builder().compute("assets/examples/compute/fluid/advectX.glsl").build();
        if(!advectX.isCompiled())System.out.println(advectX.getLog());

        advectY=Shader.builder().compute("assets/examples/compute/fluid/advectY.glsl").build();
        if(!advectY.isCompiled())System.out.println(advectY.getLog());



        fill = Shader.builder().compute("assets/examples/compute/fluid/fill.glsl").build();
        if(!fill.isCompiled())System.out.println(fill.getLog());
    }

    public void update(float delta){
        Texture temp = density;
        density = density_prev;
        density_prev = temp;
        advect(delta, helper, density_prev, u, v);
        temp=density;density=helper;helper=temp;

        temp = u;u = u_prev;u_prev = temp;
        temp = v;v = v_prev;v_prev = temp;
        advectVelocity(delta);

//        project(u, v, u_prev);
    }


    private void advect(float delta, Texture d, Texture d0, Texture u, Texture v){
        advect.use();
        d.bindAsImage(0);
        d0.bindAsImage(1);
        u.bindAsImage(2);
        v.bindAsImage(3);
        advect.setUniform("delta", delta);
        advect.setUniform("size", width);
        advect.dispatch(width/8, height/8, 1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);
    }
    private void advectVelocity(float delta){
        advectX.use();
        u.bindAsImage(0);
        u_prev.bindAsImage(1);
        v_prev.bindAsImage(2);
        advectX.setUniform("delta", delta);
        advectX.setUniform("size", width);
        advectX.dispatch(width/8, height/8, 1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);

        advectY.use();
        v.bindAsImage(0);
        u_prev.bindAsImage(1);
        v_prev.bindAsImage(2);
        advectY.setUniform("delta", delta);
        advectY.setUniform("size", width);
        advectY.dispatch(width/8, height/8, 1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);
    }
    private void project(Texture u, Texture v, Texture p){
        project.use();
        u.bindAsImage(0);
        v.bindAsImage(1);
        p.bindAsImage(2);
        project.setUniform("size", width);
        project.dispatch(width/8, height/8, 1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);
    }

    public void add(int x,int y,int px,int py,float delta,int size){
        density.bindAsImage(0);
        u.bindAsImage(1);
        v.bindAsImage(2);
        fill.use();
        fill.setUniform("size", new Vector2i(width, height));
        fill.setUniform("deltaTime", delta);
        fill.setUniform("offset", new Vector2i(x-size/2,y-size/2));
        fill.setUniform("velocity", new Vector2f(x-px,y-py).div(10));
        fill.dispatch(size, size, 1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);
    }
}
