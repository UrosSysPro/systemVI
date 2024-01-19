#version 330 core

//layout(location=0) out vec4 ColoBuffer;
//layout(location=1) out vec4 NormalBuffer;

out vec4 ColoBuffer;
out vec4 NormalBuffer;

in struct VERTEX_OUT{
    vec3 position;
    vec3 normal;
    vec3 tangent;
    vec3 bitangent;
    vec2 texCoords;
}vertexOut;

void main(){
    ColoBuffer=vec4(vertexOut.texCoords,0.5,1.0);
    NormalBuffer=vec4(vertexOut.normal/2+0.5,1.0);
}