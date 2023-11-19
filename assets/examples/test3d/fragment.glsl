#version 330 core

out vec4 pixel;

in vec3 worldPostion;

uniform vec4 color;
uniform vec3 lightColor;
uniform vec3 lightPosition;

void main(){
    pixel=vec4(vec3(length(worldPostion-lightPosition)/7.0),1.0);
}