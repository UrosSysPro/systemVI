#version 330

layout(location=0) in vec3 position;

out vec3 worldPosition;

uniform mat4 view;
uniform mat4 projection;

void main(){
    worldPosition = position;
    gl_Position = projection * view * vec4(position,1.0);
//    gl_Position =  vec4(position,1.0);
}
