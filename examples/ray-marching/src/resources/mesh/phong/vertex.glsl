#version 330

layout(location=0) in vec3 position;
layout(location=1) in vec3 normal;

out vec3 worldPosition;
out vec3 vNormal;

uniform mat4 view;
uniform mat4 projection;

void main(){
    worldPosition = position;
    vNormal = normal;
    gl_Position = projection * view * vec4(position,1.0);
}
