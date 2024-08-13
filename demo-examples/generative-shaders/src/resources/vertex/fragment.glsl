#version 460 core

out vec4 FragColor;
uniform ivec2 grid;
in struct VERTEX_DATA{
    vec2 uv;
    vec3 worldPosition;
    vec3 modelPosition;
}vertexOut;

void main(){
    vec2 uv=vertexOut.uv*vec2(grid-1);
    FragColor=vec4(fract(uv.x),fract(uv.y),0.0,1.0);
}