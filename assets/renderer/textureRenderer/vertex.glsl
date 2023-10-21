#version 330 core
layout(location=0)in vec2 aPosition;
layout(location=1)in vec2 aTexCoord;

out vec2 vTexCoord;

uniform mat4 view;
uniform mat4 projection;

void main(){
    vTexCoord=aTexCoord;
    gl_Position=projection*view*vec4(aPosition,0.0,1.0);
}