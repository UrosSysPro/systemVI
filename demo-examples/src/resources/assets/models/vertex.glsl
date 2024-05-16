#version 330 core

layout(location=0)in vec3 position;
layout(location=1)in vec3 tangent;
layout(location=2)in vec3 bitangent;
layout(location=3)in vec3 normal;

uniform mat4 view;
uniform mat4 projection;

out VERTEX_OUT{
    vec3 tangent,bitangent,normal;
}vertexOut;

void main(){
    vertexOut.tangent=tangent;
    vertexOut.bitangent=bitangent;
    vertexOut.normal=normal;

    gl_Position=projection*view*vec4(position,1.0);
}