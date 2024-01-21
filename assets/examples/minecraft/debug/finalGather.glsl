#version 330 core

in vec2 vTexCoord;
uniform sampler2D t0;
uniform sampler2D normalBuffer;
uniform sampler2D positionBuffer;
uniform vec3 cameraPosition;
uniform vec3 lightPosition;

out vec4 FragColor;

void main(){
    FragColor=texture(t0,vTexCoord);
}