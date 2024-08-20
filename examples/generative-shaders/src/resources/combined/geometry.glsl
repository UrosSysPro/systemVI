#version 410 core
layout (points) in;
layout (triangle_strip, max_vertices = 4) out;


uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;
void main() {
    mat4 combined=projection*view*model;
    float offset=0.025;
    gl_Position = combined * (gl_in[0].gl_Position + vec4(offset   , offset, 0.0, 0.0));
    EmitVertex();

    gl_Position = combined * (gl_in[0].gl_Position + vec4(-offset, offset, 0.0, 0.0));
    EmitVertex();

    gl_Position = combined * (gl_in[0].gl_Position + vec4(offset,    -offset, 0.0, 0.0));
    EmitVertex();

    gl_Position = combined * (gl_in[0].gl_Position + vec4(   -offset,    -offset, 0.0, 0.0));
    EmitVertex();

    EndPrimitive();
}