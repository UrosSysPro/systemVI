#version 330 core

in vec4 vColor;
in vec2 vTexCoords;

out vec4 FragColor;

uniform sampler2D wall;

void main(){
    FragColor=texture(wall,vTexCoords);
}