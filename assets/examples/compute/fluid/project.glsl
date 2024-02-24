#version 460 core

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;

layout (r16f, binding = 0) uniform image2D u_texture;
layout (r16f, binding = 1) uniform image2D v_texture;
layout (r16f, binding = 2) uniform image2D p_texture;
//layout (r16f, binding = 3) uniform image2D div_texture;

uniform float delta;
uniform int size;

float readP(ivec2 position){
    if(position.x>size)position.x-=size;
    if(position.x<0)position.x+=size;
    if(position.y>size)position.y-=size;
    if(position.y<0)position.y+=size;
    return imageLoad(p_texture,position).x;
}
float readU(ivec2 position){
    if(position.x>size)position.x-=size;
    if(position.x<0)position.x+=size;
    if(position.y>size)position.y-=size;
    if(position.y<0)position.y+=size;
    return imageLoad(p_texture,position).x;
}
float readV(ivec2 position){
    if(position.x>size)position.x-=size;
    if(position.x<0)position.x+=size;
    if(position.y>size)position.y-=size;
    if(position.y<0)position.y+=size;
    return imageLoad(p_texture,position).x;
}

void main() {
    ivec2 position = ivec2(gl_GlobalInvocationID.xy);
    float h = 1.0/size;
    int i=position.x;
    int j=position.y;
    float u=imageLoad(u_texture,position).x;
    float v=imageLoad(v_texture,position).x;

//    div[i][j] = -0.5f*h*(u[i+1][j]-u[i-1][j]+v[i][j+1]-v[i][j-1]);
//    p[i][j]=0f;

//    float div = -0.5*h*(readU(ivec2(i+1,j))-readU(ivec2(i-1,j))+readV(ivec2(i,j+1))-readV(ivec2(i,j-1)));
//    imageStore(p_texture,position,vec4(0.0));

//        p[i][j] = (div[i][j] + p[i-1][j] + p[i+1][j] + p[i][j-1] + p[i][j+1])/4;

//    for(int k=0;k<20;k++){
//        barrier();
//        float p=(div+readP(ivec2(i-1,j))+readP(ivec2(i-1,j))+readP(ivec2(i,j-1))+readP(ivec2(i,j+1)))/4.0;
//        imageStore(p_texture,position,vec4(p));
//    }
//    barrier();


//    u[i][j] -= 0.5*(p[i+1][j]-p[i-1][j])/h;
//    v[i][j] -= 0.5*(p[i][j+1]-p[i][j-1])/h;

//    u-=0.5*(readP(position+ivec2(1,0))-readP(position-ivec2(1,0)))/h;
//    v-=0.5*(readP(position+ivec2(0,1))-readP(position-ivec2(0,1)))/h;
    imageStore(u_texture,position,vec4(u));
    imageStore(v_texture,position,vec4(v));
}
