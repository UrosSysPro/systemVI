#version 330 core
layout (location = 0) in vec3 aPos;

out vec3 TexCoords;

uniform mat4 projection;
uniform mat4 view;
uniform vec3 position;

mat4 translate(vec3 position) {
    return mat4(
    vec4(1.0, 0.0, 0.0, 0.0),
    vec4(0.0, 1.0, 0.0, 0.0),
    vec4(0.0, 0.0, 1.0, 0.0),
    vec4(position, 1.0)
    );
}

void main() {
    TexCoords = aPos;
    gl_Position = projection * view *translate(position)* vec4(aPos, 1.0);
}