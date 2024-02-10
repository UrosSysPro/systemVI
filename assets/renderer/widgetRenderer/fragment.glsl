#version 330 core

in VERTEX_OUT{
    vec3 position;
    vec4 color;
    float borderRadius;
    float blur;
}vertexOut;

out vec4 FragColor;

void main(){
    FragColor=vertexOut.color;
}