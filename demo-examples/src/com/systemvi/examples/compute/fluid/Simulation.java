package com.systemvi.examples.compute.fluid;

import com.systemvi.engine.shader.Shader;
import com.systemvi.engine.texture.Format;
import com.systemvi.engine.texture.Texture;
import com.systemvi.engine.utils.Utils;

import javax.rmi.CORBA.Util;

import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL42.*;

public class Simulation {
    public Texture density, density_prev, u, u_prev, v, v_prev;
    public Shader project, advect, advectX, advectY;
    public int width, height;

    public Simulation(int width, int height){
        this.width = width;
        this.height = height;

        density = new Texture(width, height, Format.R16F);
        density_prev = new Texture(width, height, Format.R16F);
        u = new Texture(width, height, Format.R16F);
        u_prev = new Texture(width, height, Format.R16F);
        v = new Texture(width, height, Format.R16F);
        v_prev = new Texture(width, height, Format.R16F);

//        project=Shader.builder()
//            .compute("assets/compute/project.glsl")
//            .build();
        advect=Shader.builder()
            .compute("assets/examples/compute/fluid/advect.glsl")
            .build();
//        advectX=Shader.builder()
//            .compute("assets/compute/project.glsl")
//            .build();
//        advectY=Shader.builder()
//            .compute("assets/compute/project.glsl")
//            .build();
    }

    public void update(float delta){
        Texture temp = density;
        density = density_prev;
        density_prev = temp;

        advect(delta, density, density_prev, u, v);

//        temp = u;
//        u = u_prev;
//        u_prev = temp;
//        temp = v;
//        v = v_prev;
//        v_prev = temp;
//
//        advectVelocity(delta);
//        project(u, v, u_prev, v_prev);
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
        u.bind(0);
        glBindImageTexture(0, u.getId(), 0, false, 0, GL_READ_WRITE, GL_R16F);
        u_prev.bind(1);
        glBindImageTexture(1, u_prev.getId(), 0, false, 0, GL_READ_WRITE, GL_R16F);
        v_prev.bind(2);
        glBindImageTexture(2, v_prev.getId(), 0, false, 0, GL_READ_WRITE, GL_R16F);
        advectX.setUniform("delta", delta);
        advectX.setUniform("size", width);
        advectX.dispatch(width, height, 1);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);

        advectY.use();
        v.bind(0);
        glBindImageTexture(0, v.getId(), 0, false, 0, GL_READ_WRITE, GL_R16F);
        u_prev.bind(1);
        glBindImageTexture(1, u_prev.getId(), 0, false, 0, GL_READ_WRITE, GL_R16F);
        v_prev.bind(2);
        glBindImageTexture(2, v_prev.getId(), 0, false, 0, GL_READ_WRITE, GL_R16F);
        advectY.setUniform("delta", delta);
        advectY.setUniform("size", width);
        advectY.dispatch(width, height, 1);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
    }

    private void project(Texture u, Texture v, Texture p, Texture div){
        project.use();
        u.bind(0);
        glBindImageTexture(0, u.getId(), 0, false, 0, GL_READ_WRITE, GL_R16F);
        v.bind(1);
        glBindImageTexture(1, v.getId(), 0, false, 0, GL_READ_WRITE, GL_R16F);
        p.bind(2);
        glBindImageTexture(2, p.getId(), 0, false, 0, GL_READ_WRITE, GL_R16F);
        div.bind(3);
        glBindImageTexture(3, div.getId(), 0, false, 0, GL_READ_WRITE, GL_R16F);
        project.setUniform("size", width);
        project.dispatch(width, height, 1);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
    }
}
