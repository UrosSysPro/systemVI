#version 460 core

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;

layout (r16f, binding = 0) uniform image2D p_texture;
layout (r16f, binding = 1) uniform image2D p_prev_texture;
layout (r16f, binding = 2) uniform image2D div_texture;

//float readP(ivec2 position){
//    int size=imageSize(p_prev_texture).x;
//    if(position.x>size)position.x-=size;
//    if(position.x<0)position.x+=size;
//    if(position.y>size)position.y-=size;
//    if(position.y<0)position.y+=size;
//    return imageLoad(p_prev_texture,position).x;
//}
float readP(in ivec2 position){
    ivec2 size=imageSize(p_texture);
    ivec2 p=ivec2(0);
    p.x=int(mod(position.x+5*size.x,size.x));
    p.y=int(mod(position.y+5*size.y,size.y));
    return imageLoad(p_prev_texture,position).x;
}

void main() {
    int size=imageSize(p_texture).x;
    ivec2 position = ivec2(gl_GlobalInvocationID.xy);
    int i=position.x;
    int j=position.y;

    //        p[i][j] = (div[i][j] + p[i-1][j] + p[i+1][j] + p[i][j-1] + p[i][j+1])/4;
    float p=(imageLoad(div_texture,position).x+readP(ivec2(i-1,j))+readP(ivec2(i+1,j))+readP(ivec2(i,j-1))+readP(ivec2(i,j+1)))/4.0;
    imageStore(p_texture,position,vec4(p));
}
