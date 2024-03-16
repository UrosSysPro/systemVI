#version 330 core

in vec4 vColor;
uniform mat4 view;

out vec4 FragColor;

void main(){
    FragColor=vColor;
}