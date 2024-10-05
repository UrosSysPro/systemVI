#version 330


uniform mat4 view;
uniform mat4 projection;


layout(location=0)in vec2 position;

out vec2 p;

void main(){
    p=position;
    gl_Position=projection*view*vec4(position,0.0,1.0);
}