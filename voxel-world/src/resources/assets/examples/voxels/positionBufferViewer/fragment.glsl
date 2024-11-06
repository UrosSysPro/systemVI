#version 330

out vec4 FragColor;

in vec2 uv;

uniform sampler2D positionBuffer;

uniform ivec3 worldSize;

void main(){
    vec2 samplepoint=vec2(uv.x,uv.y);
    FragColor=texture(positionBuffer,samplepoint)/vec4(worldSize,1.0);
}