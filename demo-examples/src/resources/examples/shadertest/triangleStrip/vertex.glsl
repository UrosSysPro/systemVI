#version 330 core

void main(){
    vec4 positions[4];
    float size=0.5;
    positions[0]=vec4(-size,-size,0.0,1.0);
    positions[0]=vec4( size,-size,0.0,1.0);
    positions[0]=vec4( size, size,0.0,1.0);
    positions[0]=vec4(-size, size,0.0,1.0);

    gl_Position=positions[gl_VertexID];
}