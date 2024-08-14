#version 330 core

uniform ivec2 grid;

//out struct VERTEX_DATA{
//    vec2 uv;
//    vec3 worldPosition;
//    vec3 modelPosition;
//}vertexOut;

void main(){
    int index=gl_VertexID;

    vec2 point=vec2(
        float(index%grid.x),
        float(index/grid.x)
    );

    gl_Position=vec4(point,0.0,1.0);
}