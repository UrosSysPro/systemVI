#version 330

out vec4 FragColor;

in vec2 uv;

uniform sampler2D positionBuffer;
uniform sampler2D normalBuffer;
uniform sampler2D uvBuffer;
uniform sampler2D depthBuffer;
uniform sampler2D textureBuff;

void main(){
    vec2 samplepoint=vec2(uv.x,1.0-uv.y);
    samplepoint=texture(uvBuffer,samplepoint).xy;
    FragColor=texture(textureBuff,samplepoint);
}