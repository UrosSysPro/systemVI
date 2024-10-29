#version 330

out vec4 FragColor;

in vec2 uv;

uniform sampler2D positionBuffer;

void main(){
    vec2 samplepoint=vec2(uv.x,1.0-uv.y);
    FragColor=texture(positionBuffer,samplepoint)/vec4(32,32,32,1);
}