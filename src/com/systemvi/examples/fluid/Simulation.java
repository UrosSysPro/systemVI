package com.systemvi.examples.fluid;

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


    private void diffuse (int b, float[][] x, float[][] x0, float diff, float dt )
    {
        int i, j, k;
        float a=dt*diff*width*width;
        for ( k=0 ; k<20 ; k++ ) {
            for ( i=1 ; i<=width ; i++ ) {
                for ( j=1 ; j<=height ; j++ ) {
                    x[i][j] = (x0[i][j] + a*(x[i-1][j]+x[i+1][j]+
                        x[i][j-1]+x[i][j+1]))/(1+4*a);
                }
            }
//            set_bnd ( N, b, x );
        }
    }


    private void advect ( int N, int b, float[][] d, float[][] d0, float[][] u, float[][] v, float dt )
    {
        int i, j, i0, j0, i1, j1;
        float x, y, s0, t0, s1, t1, dt0;
        dt0 = dt*N;
        for ( i=1 ; i<=N ; i++ ) {
            for ( j=1 ; j<=N ; j++ ) {
                x = i-dt0*u[i][j]; y = j-dt0*v[i][j];
                if (x<0.5) x=0.5f; if (x>N+0.5) x=N+ 0.5f; i0=(int)x; i1=i0+1;
                if (y<0.5) y=0.5f; if (y>N+0.5) y=N+ 0.5f; j0=(int)y; j1=j0+1;
                s1 = x-i0; s0 = 1-s1; t1 = y-j0; t0 = 1-t1;
                d[i][j] = s0*(t0*d0[i0][j0]+t1*d0[i0][j1])+
                    s1*(t0*d0[i1][j0]+t1*d0[i1][j1]);
            }
        }
//        set_bnd ( N, b, d );
    }


    private void project (float[][] u, float[][] v, float[][] p, float[][] div )
    {
        int i, j, k;
        float h;
        h = 1.0f/width;
        for ( i=1 ; i<=width ; i++ ) {
            for ( j=1 ; j<=height ; j++ ) {
                div[i][j] = -0.5f*h*(u[i+1][j]-u[i-1][j]+
                    v[i][j+1]-v[i][j-1]);
                p[i][j]=0f;
            }
        }
//        set_bnd ( N, 0, div ); set_bnd ( N, 0, p );
        for ( k=0 ; k<20 ; k++ ) {
            for ( i=1 ; i<=width ; i++ ) {
                for ( j=1 ; j<=height ; j++ ) {
                    p[i][j] = (div[i][j]+p[i-1][j]+p[i+1][j]+
                        p[i][j-1]+p[i][j+1])/4;
                }
            }
//            set_bnd ( N, 0, p );
        }
        for ( i=1 ; i<=width ; i++ ) {
            for ( j=1 ; j<=height ; j++ ) {
                u[i][j] -= 0.5*(p[i+1][j]-p[i-1][j])/h;
                v[i][j] -= 0.5*(p[i][j+1]-p[i][j-1])/h;
            }
        }
//        set_bnd ( N, 1, u ); set_bnd ( N, 2, v );
    }

//    void set_bnd ( int N, int b, float[][] x) {
//        int i;
//        for ( i=1 ; i<=N ; i++ ) {
//            x[0][i]=(b==1)?(–x[1][i]):(x[1][i]);
//            x[N+1][i]=b==1?–x[N][i]:x[N][i];
//            x[i][0]=b==2?–x[i][1] : x[i][1];
//            x[i][N+1]=b==2? –x[i][N] : x[i][N];
//        }
//        x[IX(0 ,0 )] = 0.5*(x[IX(1,0 )]+x[IX(0 ,1)]);
//        x[IX(0 ,N+1)] = 0.5*(x[IX(1,N+1)]+x[IX(0 ,N )]);
//        x[IX(N+1,0 )] = 0.5*(x[IX(N,0 )]+x[IX(N+1,1)]);
//        x[IX(N+1,N+1)] = 0.5*(x[IX(N,N+1)]+x[IX(N+1,N )]);
//    }
}
