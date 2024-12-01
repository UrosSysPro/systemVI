#version 330 core

layout(location=0)in vec3 position;
layout(location=1)in vec3 normal;
layout(location=2)in vec2 uv;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

out struct VERTEX_OUT{
    vec3 position;
    vec3 normal;
    vec2 uv;
} vertexOut;


void main(){
    vertexOut.position=vec3(model*vec4(position,1.0));
    vertexOut.normal=normal;
    vertexOut.uv=uv;
    gl_Position=projection*view*model*vec4(position,1.0);
}