#version 330 core

layout(location=0)in vec3 position;
layout(location=1)in vec3 tangent;
layout(location=2)in vec3 bitangent;
layout(location=3)in vec3 normal;
layout(location=4)in vec2 texCoords;
layout(location=5)in vec4 color;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 transform;
uniform mat4 model;

out VERTEX_OUT{
    vec3 tangent,bitangent,normal,modelPosition,worldPosition;
    vec2 texCoords;
    vec4 color;
}vertexOut;

void main(){
    vec4 modelPosition=model*vec4(position,1.0);
    vec4 worldPosition=transform*modelPosition;
    gl_Position=projection*view*worldPosition;

    vertexOut.tangent=tangent;
    vertexOut.bitangent=bitangent;
    vertexOut.normal=normal;
    vertexOut.modelPosition=modelPosition.xyz;
    vertexOut.worldPosition=worldPosition.xyz;
    vertexOut.texCoords=texCoords;
    vertexOut.color=color;
}