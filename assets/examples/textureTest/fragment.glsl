#version 330 core

in vec4 vColor;
in vec2 vTexCoords;

out vec4 FragColor;

uniform sampler2D wall;

void main(){
//    FragColor=vec4(vTexCoords,0.5,1.0);
    vec3 texel=texture(wall,vTexCoords*1.0).rgb;
//    texel.rg=texel.gr;
    texel*=vColor.rgb;
    FragColor=vec4(texel,1.0);
}