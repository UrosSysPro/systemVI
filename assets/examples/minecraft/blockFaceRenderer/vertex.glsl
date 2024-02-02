#version 330 core

layout(location=0)in vec2 position;
layout(location=1)in vec3 normal;
layout(location=2)in vec3 tangent;
layout(location=3)in vec3 bitangent;
layout(location=4)in vec2 texCoords;

layout(location=5)in mat4 model;
layout(location=9)in vec2 uv;

uniform mat4 view;
uniform mat4 projection;

out struct VERTEX_OUT{
    vec3 position;
    vec3 normal;
    vec3 tangent;
    vec3 bitangent;
    vec2 texCoords;
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
    vec2 uvs[4];
//    uvs[0]=vec2(0.0,1.0);
//    uvs[1]=vec2(1.0,0.0);
//    uvs[2]=vec2(0.0,0.0);
//    uvs[3]=vec2(1.0,1.0);

    uvs[0]=vec2(1.0,1.0);
    uvs[1]=vec2(0.0,0.0);
    uvs[2]=vec2(1.0,0.0);
    uvs[3]=vec2(0.0,1.0);

    vertexOut.texCoords=(uv+uvs[gl_VertexID]*spriteSize-vec2(0.0,1.0))/spriteSheetSize;

    gl_Position=projection*view*model*vec4(position,0.0,1.0);
}