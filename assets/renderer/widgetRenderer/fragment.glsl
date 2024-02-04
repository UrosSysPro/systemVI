#version 330 core

in VERTEX_OUT{
    vec3 position;
    vec4 color;
    float borderRadius;
    float blur;
}vertexOut;

out vec4 FragColor;

void main(){
    FragColor=vec4(vertexOut.position.xy,0.5,1.0);
}