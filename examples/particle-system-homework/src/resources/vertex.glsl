#version 330


uniform mat4 view;
uniform mat4 projection;

layout(location=0)in vec2 position;
layout(location=1)in vec4 aColor;

out vec4 vColor;

void main(){
    vColor=aColor;
    gl_Position=projection*view*vec4(position,0.0,1.0);
}