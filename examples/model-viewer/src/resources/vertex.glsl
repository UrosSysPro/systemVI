#version 330 core

layout(location=0)in vec3 position;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

out vec3 pos;

void main(){
    pos=vec3(model*vec4(position,1.0));
    gl_Position=projection*view*model*vec4(position,1.0);
}