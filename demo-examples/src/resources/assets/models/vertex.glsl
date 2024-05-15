#version 330 core

layout(location=0)in vec3 position;
layout(location=1)in vec3 tangent;
layout(location=2)in vec3 bitangent;
layout(location=3)in vec3 normal;

uniform mat4 view;
uniform mat4 projection;

void main(){
    gl_Position=projection*view*vec4(position,1.0);
}