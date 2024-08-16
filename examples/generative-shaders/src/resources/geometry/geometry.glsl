#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 4) out;


uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

//in struct VERTEX_DATA{
//    vec2 uv;
//    vec3 worldPosition;
//    vec3 modelPosition;
//}vertexOut[];

//out struct GEOMETRY_DATA{
//    vec2 uv;
//    vec3 worldPosition;
//    vec3 modelPosition;
//}geometryOut;

void main() {
    mat4 combined=projection*view;
//    combined=mat4(
//        vec4(1,0,0,0),
//        vec4(0,1,0,0),
//        vec4(0,0,1,0),
//        vec4(0,0,0,1)
//    );
    float offset=-0.5;
    gl_Position = combined * (gl_in[0].gl_Position + vec4(-offset, offset, 0.0, 0.0));
    EmitVertex();

    gl_Position = combined * (gl_in[0].gl_Position + vec4( offset, offset, 0.0, 0.0));
    EmitVertex();

    gl_Position = combined * (gl_in[0].gl_Position + vec4( offset, -offset, 0.0, 0.0));
    EmitVertex();

    gl_Position = combined * (gl_in[0].gl_Position + vec4( -offset,  -offset, 0.0, 0.0));
    EmitVertex();

    EndPrimitive();
}