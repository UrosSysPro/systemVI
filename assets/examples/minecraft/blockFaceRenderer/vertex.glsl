#version 330 core

layout(location=0)in vec2 position;
layout(location=1)in vec3 normal;
layout(location=2)in vec3 tangent;
layout(location=3)in vec3 bitangent;
layout(location=4)in vec2 texCoords;

layout(location=5)in mat4 model;

uniform mat4 view;
uniform mat4 projection;

out struct VERTEX_OUT{
    vec3 position;
    vec3 normal;
    vec3 tangent;
    vec3 bitangent;
    vec2 texCoords;
}vertexOut;

void main(){
    mat3 modelRotation=mat3(model);

    vec4 vertexPosition=model*vec4(position,0.0,1.0);


    vertexOut.position=vertexPosition.xyz;
    vertexOut.normal=modelRotation*normal;
    vertexOut.tangent=modelRotation*tangent;
    vertexOut.bitangent=modelRotation*bitangent;
    vertexOut.texCoords=texCoords;

    gl_Position=projection*view*model*vec4(position,0.0,1.0);
}