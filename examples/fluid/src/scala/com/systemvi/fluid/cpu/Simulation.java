package com.systemvi.fluid.cpu;

import com.systemvi.engine.renderers.ShapeRenderer;
import org.joml.Vector4f;

public class Simulation {
    private int width,height;
    private float[][] dens,u,v,dens_prev,u_prev,v_prev;

    public Simulation(int width,int height){
        this.width=width;
        this.height=height;
        dens=new float[width][height];
        dens_prev=new float[width][height];
        u=new float[width][height];
        u_prev=new float[width][height];
        v=new float[width][height];
        v_prev=new float[width][height];
    }

    public void dens_step (float diff,float dt )
    {
//        add_source ( N, x, x0, dt );
        float[][] temp;
        temp=dens;
        dens=dens_prev;
        dens_prev=temp;
        diffuse (  0, dens, dens_prev, diff, dt );
        temp=dens;
        dens=dens_prev;
        dens_prev=temp;
        advect ( 0, dens,dens_prev, u, v, dt );
    }
    public void vel_step(float visc, float dt ){
//        add_source ( N, u, u0, dt );
//        add_source ( N, v, v0, dt );
        float[][] temp;
        temp=u;
        u=u_prev;
        u_prev=temp;
        diffuse (1, u, u_prev, visc, dt );
        temp=v;
        v=v_prev;
        v_prev=temp;
        diffuse (2, v, v_prev, visc, dt );
        project ( u, v, u_prev, v_prev );
        temp=u;
        u=u_prev;
        u_prev=temp;
        temp=v;
        v=v_prev;
        v_prev=temp;
        advect (  1, u, u_prev, u_prev, v_prev, dt );
        advect (  2, v, v_prev, u_prev, v_prev, dt );
        project (  u, v, u_prev, v_prev );
    }

    private void diffuse (int b, float[][] x, float[][] x0, float diff, float dt )
    {
        int i, j, k;
        float a=dt*diff*width*width;
        for ( k=0 ; k<20 ; k++ ) {
            for ( i=1 ; i<width-1 ; i++ ) {
                for ( j=1 ; j<height-1 ; j++ ) {
                    x[i][j] = (x0[i][j] + a*(x[i-1][j]+x[i+1][j]+
                        x[i][j-1]+x[i][j+1]))/(1+4*a);
                }
            }
            set_bnd ( width-2, b, x );
        }
    }


    private void advect (int b, float[][] d, float[][] d0, float[][] u, float[][] v, float dt )
    {
        int i, j, i0, j0, i1, j1;
        float x, y, s0, t0, s1, t1, dt0;
        dt0 = dt*width;
        for ( i=1 ; i<width-1; i++ ) {
            for ( j=1 ; j<height-1; j++ ) {
                x = i-dt0*u[i][j]; y = j-dt0*v[i][j];
                if (x<0.5) x=0.5f; if (x>width+0.5f) x=width+ 0.5f; i0=(int)x; i1=i0+1;
                if (y<0.5) y=0.5f; if (y>height+0.5f) y=height+ 0.5f; j0=(int)y; j1=j0+1;
                s1 = x-i0; s0 = 1-s1; t1 = y-j0; t0 = 1-t1;
                d[i][j] = s0*(t0*d0[i0][j0]+t1*d0[i0][j1])+
                    s1*(t0*d0[i1][j0]+t1*d0[i1][j1]);
            }
        }
        set_bnd ( width-2, b, d );
    }


    private void project (float[][] u, float[][] v, float[][] p, float[][] div )
    {
        int i, j, k;
        float h;
        h = 1.0f/width;
        for ( i=1 ; i<width -1; i++ ) {
            for ( j=1 ; j<height-1 ; j++ ) {
                div[i][j] = -0.5f*h*(u[i+1][j]-u[i-1][j]+
                    v[i][j+1]-v[i][j-1]);
                p[i][j]=0f;
            }
        }
        set_bnd ( width-2, 0, div ); set_bnd ( width-2, 0, p );
        for ( k=0 ; k<20 ; k++ ) {
            for ( i=1 ; i<width-1 ; i++ ) {
                for ( j=1 ; j<height-1 ; j++ ) {
                    p[i][j] = (div[i][j]+p[i-1][j]+p[i+1][j]+
                        p[i][j-1]+p[i][j+1])/4;
                }
            }
            set_bnd ( width-2, 0, p );
        }
        for ( i=1 ; i<width-1 ; i++ ) {
            for ( j=1 ; j<height-1 ; j++ ) {
                u[i][j] -= 0.5*(p[i+1][j]-p[i-1][j])/h;
                v[i][j] -= 0.5*(p[i][j+1]-p[i][j-1])/h;
            }
        }
        set_bnd ( width-2, 1, u ); set_bnd ( width-2, 2, v );
    }

    private void set_bnd ( int N, int b, float[][] x) {
        int i;
        for ( i=1 ; i<=N ; i++ ) {
            x[0][i]=(b==1)?(-x[1][i]):(x[1][i]);
            x[N+1][i]=b==1?-x[N][i]:x[N][i];
            x[i][0]=b==2?-x[i][1] : x[i][1];
            x[i][N+1]=b==2? -x[i][N] : x[i][N];
        }
        x[0][0] = 0.5f*(x[1][0]+x[0][1]);
        x[0][N+1] = 0.5f*(x[1][N+1]+x[0][N]);
        x[N+1][0] = 0.5f*(x[N][0]+x[N+1][1]);
        x[N+1][N+1] = 0.5f*(x[N][N+1]+x[N+1][N]);
    }
    public void draw(ShapeRenderer renderer,int size){
        Vector4f color=new Vector4f(1);
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                color.set(dens[i][j]);
                renderer.rect(i*size,j*size,size,size,color);
            }
        }
    }
    public void add(int x,int y,int range){
        for(int i=-range/2;i<=range/2;i++){
            for(int j=-range/2;j<=range/2;j++){
                if(x+i<0||x+i>=width)continue;
                if(y+j<0||y+j>=height)continue;
                dens[x+i][y+j]=1;
            }
        }
    }
    public void addVelocity(int x,int y,float dx,float dy,int range){
        for(int i=-range/2;i<=range/2;i++){
            for(int j=-range/2;j<=range/2;j++){
                if(x+i<0||x+i>=width)continue;
                if(y+j<0||y+j>=height)continue;
                u[x+i][y+j]=dx;
                v[x+i][y+j]=dy;
            }
        }
    }
}
