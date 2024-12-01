#version 330 core

layout(location=0)in vec3 position;
layout(location=1)in vec3 normal;
layout(location=2)in vec2 uv;
layout(location=3)in vec4 color;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

out struct VERTEX_OUT{
    vec3 position;
    vec3 normal;
    vec2 uv;
    vec4 color;
} vertexOut;


void main(){
    vertexOut.position=vec3(model*vec4(position,1.0));
    vertexOut.normal=normal;
    vertexOut.uv=uv;
    vertexOut.color=color;
    gl_Position=projection*view*model*vec4(position,1.0);
}