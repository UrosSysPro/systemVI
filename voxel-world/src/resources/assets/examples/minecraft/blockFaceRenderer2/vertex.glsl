#version 330 core

layout(location=0)in vec2 position;
layout(location=1)in vec3 normal;
layout(location=2)in vec3 tangent;
layout(location=3)in vec3 bitangent;
layout(location=4)in vec2 texCoords;

layout(location=5)in mat4 model;
layout(location=9)in vec2 uv;
layout(location=10)in vec4 lightLevel;

uniform mat4 view;
uniform mat4 projection;

out struct VERTEX_OUT{
    vec3 position;
    vec3 normal;
    vec3 tangent;
    vec3 bitangent;
    vec2 texCoords;
    float lightLevel;
}vertexOut;

vec2 spriteSize=vec2(16.0,16.0);
vec2 spriteSheetSize=vec2(160.0,256.0);

void main(){
    mat3 modelRotation=mat3(model);

    vec4 vertexPosition=model*vec4(position,0.0,1.0);


    vertexOut.position=vertexPosition.xyz;
    vertexOut.normal=modelRotation*normal;
    vertexOut.tangent=modelRotation*tangent;
    vertexOut.bitangent=modelRotation*bitangent;
    vertexOut.lightLevel=lightLevel[0]/15.0;
    vec2 offset=vec2(1.0,0.0)/spriteSheetSize;
    vertexOut.texCoords=(uv+texCoords*spriteSize-offset)/spriteSheetSize;

    gl_Position=projection*view*model*vec4(position,0.0,1.0);
}