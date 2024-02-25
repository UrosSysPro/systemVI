#version 460 core

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;

layout (r16f, binding = 0) uniform image2D u_texture;
layout (r16f, binding = 1) uniform image2D v_texture;
layout (r16f, binding = 2) uniform image2D u_prev_texture;
layout (r16f, binding = 3) uniform image2D v_prev_texture;
layout (r16f, binding = 4) uniform image2D p_texture;

//float readP(ivec2 position){
//    int size=imageSize(u_texture).x;
//    if(position.x>size)position.x-=size;
//    if(position.x<0)position.x+=size;
//    if(position.y>size)position.y-=size;
//    if(position.y<0)position.y+=size;
//    return imageLoad(p_texture,position).x;
//}

float readP(in ivec2 position){
    ivec2 size=imageSize(u_texture);
    ivec2 p=ivec2(0);
    p.x=int(mod(position.x+5*size.x,size.x));
    p.y=int(mod(position.y+5*size.y,size.y));
    return imageLoad(p_texture,position).x;
}

void main() {
    int size=imageSize(u_texture).x;
    ivec2 position = ivec2(gl_GlobalInvocationID.xy);
    float h = 1.0/size;
    int i=position.x;
    int j=position.y;
    float u=imageLoad(u_prev_texture,position).x;
    float v=imageLoad(v_prev_texture,position).x;

    //    u[i][j] -= 0.5*(p[i+1][j]-p[i-1][j])/h;
    //    v[i][j] -= 0.5*(p[i][j+1]-p[i][j-1])/h;

    u-=0.5*(readP(position+ivec2(1,0))-readP(position-ivec2(1,0)))/h;
    v-=0.5*(readP(position+ivec2(0,1))-readP(position-ivec2(0,1)))/h;
    imageStore(u_texture,position,vec4(u));
    imageStore(v_texture,position,vec4(v));
}
