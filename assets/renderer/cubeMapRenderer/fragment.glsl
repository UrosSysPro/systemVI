#version 330 core

out vec4 FragColor;

in vec3 position;

uniform samplerCube cubeMap;

void main(){
    vec4 color=texture(cubeMap,position);
    FragColor=color;
//    FragColor=vec4(abs(normalize(position)),1.0);
}