#version 330 core

in vec2 vTexCoord;

uniform sampler2D t0;
out vec4 FragColor;

void main(){
    float z=texture(t0,vTexCoord).r;
    float near=0.1,far=1000.0;
    float depth= (2.0 * near) / (far + near - z * (far - near));
    FragColor=vec4(depth);
}