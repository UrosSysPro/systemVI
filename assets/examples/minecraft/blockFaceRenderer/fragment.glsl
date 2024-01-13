#version 330 core

out vec4 FragColor;

in struct VERTEX_OUT{
    vec3 position;
    vec3 normal;
    vec3 tangent;
    vec3 bitangent;
    vec2 texCoords;
}vertexOut;

void main(){
    FragColor=vec4(vertexOut.normal/2+0.5,1.0);
}