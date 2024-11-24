#version 330

layout(location=0) in vec2 position;
layout(location=1) in vec4 color;
layout(location=2) in vec2 uv;
layout(location=3) in mat4 model;

out vec4 vColor;
out vec2 textureCoords;

uniform mat4 view;
uniform mat4 projection;

void main(){
    vColor=color;
    textureCoords=uv;
    gl_Position=projection*view*model*vec4(position,0.0,1.0);
}