#version 330 core

layout(location=0) in vec3 vPosition;

out vec3 position;

uniform mat4 view;
uniform mat4 projection;

void main(){
    mat4 rotation=mat4(view[0],view[1],view[2],vec4(0.0,0.0,0.0,1.0));

    vec4 p=projection*rotation*vec4(vPosition-0.5,1.0);

    position=vPosition-0.5;

    gl_Position=p;
}