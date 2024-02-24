#version 460 core

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;

layout (r16f, binding = 0) uniform image2D u_texture;
layout (r16f, binding = 1) uniform image2D u_prev_texture;
layout (r16f, binding = 2) uniform image2D v_prev_texture;

uniform float delta;

float readPreviousDensity(ivec2 position){
    int size=imageSize(u_texture).x;
    if(position.x>size)position.x-=size;
    if(position.x<0)position.x+=size;
    if(position.y>size)position.y-=size;
    if(position.y<0)position.y+=size;
    return imageLoad(u_prev_texture,position).x;
}

void main() {
    int size=imageSize(u_texture).x;
    ivec2 position = ivec2(gl_GlobalInvocationID.xy);
    float dt0 = delta * float(size);
    float u = imageLoad(u_prev_texture, position).x;
    float v = imageLoad(v_prev_texture, position).x;
    int i = position.x;
    int j = position.y;
    float x = float(i) - dt0 * u;
    float y = float(j) - dt0 * v;
    int i0 = int(x);
    int i1 = 1 + i0;
    int j0 = int(y);
    int j1 = 1 + j0;
    float s1 = x - float(i0);
    float s0 = 1.0 - s1;
    float t1 = y - float(j0);
    float t0 = 1.0 - t1;
    float d00 = readPreviousDensity(ivec2(i0, j0));
    float d01 = readPreviousDensity(ivec2(i0, j1));
    float d10 = readPreviousDensity(ivec2(i1, j0));
    float d11 = readPreviousDensity(ivec2(i1, j1));
    float d = s0 * (t0 * d00 + t1 * d01) + s1 * (t0 * d10 + t1 * d11);
    imageStore(u_texture, position, vec4(d));
}
