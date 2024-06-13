#version 460 core

uniform mat4 view;
uniform mat4 projection;

void main(){
    vec4 points[3]={
        vec4( 0.0, 0.5,0.0,1.0),
        vec4(-0.5,-0.5,0.0,1.0),
        vec4( 0.5,-0.5,0.0,1.0),
    };
    gl_Position=projection*view*points[gl_VertexID];
}