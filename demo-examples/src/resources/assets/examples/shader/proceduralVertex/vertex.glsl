#version 460 core

uniform ivec2 grid;
uniform mat4 view;
uniform mat4 projection;

void main(){
    ivec2 quadrant;
    quadrant.x=gl_VertexID%(2*grid.x+2)/2;
    quadrant.y=gl_VertexID/(2*grid.x+2);
    int index=gl_VertexID%(2*grid.x+2);
    if(index==grid.x*2){
        quadrant.x=grid.x-1;
        quadrant.y++;
    }
    if(index==grid.x*2+1){
        quadrant.x=0;
    }
    gl_Position=projection*view*(vec4(0,index%2,0,1)+vec4(vec2(quadrant),0,0));
}