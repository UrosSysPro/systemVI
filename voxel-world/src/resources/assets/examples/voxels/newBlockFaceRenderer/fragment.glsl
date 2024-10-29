#version 330

out vec3 positionBuffer;
out vec3 normalBuffer;
out vec2 uvBuffer;

in struct VERTEX_OUT{
    vec2 uv;
    vec3 tangent;
    vec3 bitangent;
    vec3 normal;
    vec3 worldPosition;
}vertexOut;

void main(){
    positionBuffer=vertexOut.worldPosition;
    normalBuffer=vertexOut.normal;
    uvBuffer=vertexOut.uv;
}