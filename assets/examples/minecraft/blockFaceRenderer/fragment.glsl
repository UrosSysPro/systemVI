#version 330 core

//layout(location=0) out vec4 ColoBuffer;
//layout(location=1) out vec4 NormalBuffer;

layout(location=0) out vec2 UvBuffer;
layout(location=1) out vec4 NormalBuffer;
layout(location=2) out vec4 PositionBuffer;

in struct VERTEX_OUT{
    vec3 position;
    vec3 normal;
    vec3 tangent;
    vec3 bitangent;
    vec2 texCoords;
}vertexOut;

uniform sampler2D normalMap;

void main(){
    mat3 TBN=mat3(vertexOut.tangent,vertexOut.bitangent,vertexOut.normal);
    vec3 normal=texture(normalMap,vertexOut.texCoords).rgb;
    normal=normal*2.0-1.0;
    normal=normalize(normal);
    normal=TBN*normal;
    normal=normal*0.5+0.5;

//    UvBuffer=vec4(vertexOut.texCoords,0.0,1.0);
    UvBuffer=vertexOut.texCoords;
    NormalBuffer=vec4(normal,1.0);
    PositionBuffer=vec4(vertexOut.position,1.0);
}