#version 330



out vec4 FragColor;

in vec2 p;

void main(){
    FragColor=vec4((p+0.5),1.0,1.0);
}