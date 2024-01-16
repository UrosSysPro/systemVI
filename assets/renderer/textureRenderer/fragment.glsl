#version 330 core

in vec2 vTexCoord;

uniform sampler2D t0;
out vec4 FragColor;

void main(){
//    FragColor=vec4(vTexCoord,0.5,1.0);
    FragColor=texture(t0,vTexCoord);
}