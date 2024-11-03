#version 330

out vec3 positionBuffer;
out vec3 normalBuffer;
out vec3 tangentBuffer;
out vec2 uvBuffer;
out float occlusionBuffer;

in struct VERTEX_OUT{
    vec2 uv;
    vec3 tangent;
    vec3 bitangent;
    vec3 normal;
    vec3 worldPosition;
    float occlusion;
}vertexOut;

void main(){
    positionBuffer=vertexOut.worldPosition;
    normalBuffer=vertexOut.tangent;
    tangentBuffer=vertexOut.normal;
    uvBuffer=vertexOut.uv;
    occlusionBuffer=vertexOut.occlusion;
}