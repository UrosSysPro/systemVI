#version 430 core

layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

vec2 size=vec2(800.0,600.0);

layout(binding = 0, std430) buffer shaderStorage {
    float data[800];
};

uniform float time;

void main() {
    int x = int(gl_GlobalInvocationID.x);
    float copy=float(x);
    copy/=size.x;
    copy*=2.0;
    float value=sin(copy+time);
    value*=100.0;
    value+=300.0;

    data[x]=value;
}