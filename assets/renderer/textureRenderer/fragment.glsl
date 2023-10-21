#version 330 core

in vec2 vTexCoord;

uniform sampler2D t0;
out vec4 FragColor;

void main(){
    FragColor=texture(t0,vTexCoord);
}