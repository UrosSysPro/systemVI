#version 330 core

in vec2 vTexCoord;

uniform sampler2D t0;
uniform sampler2D u_buffer;
uniform sampler2D v_buffer;

out vec4 FragColor;

void main(){
    FragColor=vec4(
//        texture(t0,vTexCoord).x,
        0.0,
        texture(u_buffer,vTexCoord).x*0.5+0.5,
        texture(v_buffer,vTexCoord).x*0.5+0.5,
        1.0
    );
}