#version 330 core

layout(location=0) in vec3 vPosition;

out vec3 position;

uniform mat4 view;
uniform mat4 projection;

void main(){
    vec4 p=projection*view*vec4(vPosition,1.0);

    position=p.xyz;

    gl_Position=p;
}