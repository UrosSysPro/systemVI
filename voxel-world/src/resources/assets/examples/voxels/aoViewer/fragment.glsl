#version 330

out vec4 FragColor;

in vec2 uv;

uniform sampler2D textureBuff;

void main(){
    vec2 samplepoint=vec2(uv.x,uv.y);
    float value=texture(textureBuff,samplepoint).x;
    FragColor=vec4(value);
}