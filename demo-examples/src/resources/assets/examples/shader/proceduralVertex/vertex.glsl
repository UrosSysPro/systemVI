#version 460 core

uniform ivec2 grid;
uniform mat4 view;
uniform mat4 projection;

void main(){
    vec4 points[2]={
        vec4(0,0,0,1),
        vec4(0,1,0,1)
    };

    ivec2 quadrant;
    quadrant.x=min(gl_VertexID%(2*grid.x+1),2*grid.x-1)/2;
    quadrant.y=gl_VertexID/(2*grid.x+1);
    int index=min(gl_VertexID%(2*grid.x+1),2*grid.x-1)%2;


    gl_Position=projection*view*(points[index]+vec4(vec2(quadrant),0.0,0.0));
}