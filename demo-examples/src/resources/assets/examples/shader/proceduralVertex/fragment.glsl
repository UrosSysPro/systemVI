#version 460 core

out vec4 FragColor;

in struct VERTEX_DATA{
    vec2 uv;
    vec3 worldPosition;
    vec3 modelPosition;
}vertexOut;

void main(){

    FragColor=vec4(vertexOut.uv,0.9,1.0);
}