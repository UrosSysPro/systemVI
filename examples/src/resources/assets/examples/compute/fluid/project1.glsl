#version 460 core

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;

layout (r16f, binding = 0) uniform image2D u_texture;
layout (r16f, binding = 1) uniform image2D v_texture;
layout (r16f, binding = 2) uniform image2D p_texture;
layout (r16f, binding = 3) uniform image2D div_texture;

//float readU(ivec2 position){
//    int size=imageSize(u_texture).x;
//    if(position.x>size)position.x-=size;
//    if(position.x<0)position.x+=size;
//    if(position.y>size)position.y-=size;
//    if(position.y<0)position.y+=size;
//    return imageLoad(u_texture,position).x;
//}
//float readV(ivec2 position){
//    int size=imageSize(u_texture).x;
//    if(position.x>size)position.x-=size;
//    if(position.x<0)position.x+=size;
//    if(position.y>size)position.y-=size;
//    if(position.y<0)position.y+=size;
//    return imageLoad(v_texture,position).x;
//}

float readU(in ivec2 position){
    ivec2 size=imageSize(u_texture);
    ivec2 p=ivec2(0);
    p.x=int(mod(position.x+5*size.x,size.x));
    p.y=int(mod(position.y+5*size.y,size.y));
    return imageLoad(u_texture,p).x;
}
float readV(in ivec2 position){
    ivec2 size=imageSize(u_texture);
    ivec2 p=ivec2(0);
    p.x=int(mod(position.x+5*size.x,size.x));
    p.y=int(mod(position.y+5*size.y,size.y));
    return imageLoad(v_texture,position).x;
}

void main() {
    int size=imageSize(u_texture).x;
    ivec2 position = ivec2(gl_GlobalInvocationID.xy);
    float h = 1.0/size;
    int i=position.x;
    int j=position.y;

//    div[i][j] = -0.5f*h*(u[i+1][j]-u[i-1][j]+v[i][j+1]-v[i][j-1]);
//    p[i][j]=0f;

    float div = -0.5*h*(readU(ivec2(i+1,j))-readU(ivec2(i-1,j))+readV(ivec2(i,j+1))-readV(ivec2(i,j-1)));
    imageStore(div_texture,position,vec4(div));
    imageStore(p_texture,position,vec4(0.0));
}
