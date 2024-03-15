#version 330 core

in vec2 vTexCoord;

uniform sampler2D t0;
uniform sampler2D u_buffer;
uniform sampler2D v_buffer;

out vec4 FragColor;

void main(){
    float density=texture(t0,vTexCoord).r;
    float u=texture(u_buffer,vTexCoord).r;
    float v=texture(v_buffer,vTexCoord).r;
    FragColor=vec4(0.0,u,0.0,1.0);
}