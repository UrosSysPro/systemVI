#version 330 core

in vec2 vTexCoord;

uniform sampler2D t0;
out vec4 FragColor;

void main(){
    float z=texture(t0,vTexCoord).r;
    float near=0.1,far=1000.0;
    float depth=z;
//    float depth=((1.0/z)-(1.0/near))/((1.0/far)-(1.0/near));
//    float depth=((z)-(near))/(far-near);
    FragColor=vec4(depth);
}