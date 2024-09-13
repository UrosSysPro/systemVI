#version 410 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main(){
    int index=gl_VertexID;

    vec2[4] points;
    points[0]=vec2(0,0);
    points[1]=vec2(1,0);
    points[2]=vec2(1,1);
    points[3]=vec2(0,1);

    vec2 point=points[index];

    point-=vec2(0.5);

    gl_Position=vec4(point,0.0,1.0);
}