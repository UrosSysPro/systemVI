#version 330 core

uniform vec3 position;
uniform mat4 view;
uniform mat4 projection;

void main(){
    vec4 postions[4];
    postions[0]=vec4(0.0,0.0,0.0,1.0);
    postions[1]=vec4(1.0,0.0,0.0,1.0);
    postions[2]=vec4(0.0,1.0,0.0,1.0);
    postions[3]=vec4(1.0,1.0,0.0,1.0);

    gl_Position=(postions[gl_VertexID]);
}