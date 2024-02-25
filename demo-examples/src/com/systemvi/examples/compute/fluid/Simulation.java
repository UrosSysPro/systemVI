package com.systemvi.examples.compute.fluid;

import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.w3c.dom.Text;

public class Simulation {
    public Texture density, density_prev, u, u_prev, v, v_prev,helper,p,div;
    public Shader project1,project2,project3, advect,advectX,advectY,fill;
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
        p               = new Texture(width, height, Format.R16F);
        div             = new Texture(width, height, Format.R16F);
        helper          = new Texture(width, height, Format.R16F);

        project1=Shader.builder().compute("assets/examples/compute/fluid/project1.glsl").build();
        if(!project1.isCompiled()) System.out.println(project1.getLog());
        project2=Shader.builder().compute("assets/examples/compute/fluid/project2.glsl").build();
        if(!project2.isCompiled()) System.out.println(project2.getLog());
        project3=Shader.builder().compute("assets/examples/compute/fluid/project3.glsl").build();
        if(!project3.isCompiled()) System.out.println(project3.getLog());

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
        advect(delta, density, density_prev, u, v);

        temp = u;u = u_prev;u_prev = temp;
        temp = v;v = v_prev;v_prev = temp;
        advectVelocity(delta);

        project();
    }


    private void advect(float delta, Texture d, Texture d0, Texture u, Texture v){
        advect.use();
        d.bindAsImage(0);
        d0.bindAsImage(1);
        u.bindAsImage(2);
        v.bindAsImage(3);
        advect.setUniform("delta", delta);
        advect.dispatch(width/8, height/8, 1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);
    }
    private void advectVelocity(float delta){
        advectX.use();
        u.bindAsImage(0);
        u_prev.bindAsImage(1);
        v_prev.bindAsImage(2);
        advectX.setUniform("delta", delta);
        advectX.dispatch(width/8, height/8, 1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);

        advectY.use();
        v.bindAsImage(0);
        u_prev.bindAsImage(1);
        v_prev.bindAsImage(2);
        advectY.setUniform("delta", delta);
        advectY.dispatch(width/8, height/8, 1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);
    }
    private void project(){
        project1.use();
        u.bindAsImage(0);
        v.bindAsImage(1);
        p.bindAsImage(2);
        div.bindAsImage(3);
        project1.dispatch(width/8,height/8,1);
        Utils.barrier(Utils.Barrier.IMAGE_ACCESS);

        for(int i=0;i<20;i++){
            project2.use();
            helper.bindAsImage(0);
            p.bindAsImage(1);
            div.bindAsImage(2);
            project2.dispatch(width/8,height/8,1);
            Utils.barrier(Utils.Barrier.IMAGE_ACCESS);
            Texture temp=helper;helper=p;p=temp;
        }

        Texture temp;
        temp=u_prev;u_prev=u;u=temp;
        temp=v_prev;v_prev=v;v=temp;

        project3.use();
        u.bindAsImage(0);
        v.bindAsImage(1);
        u_prev.bindAsImage(2);
        v_prev.bindAsImage(3);
        p.bindAsImage(4);
        project3.dispatch(width/8,height/8,1);
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
