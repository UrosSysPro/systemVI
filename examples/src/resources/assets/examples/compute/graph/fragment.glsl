#version 430 core

out vec4 FragColor;

in vec2 vTexCoord;

layout(binding = 0, std430) readonly buffer shaderStorage {
    float data[800];
};

vec2 size=vec2(800.0,600.0);

void main(){

    int x=int(vTexCoord.x*size.x);
    x=max(0,min(800,x));
    float value=data[x];

    int y=int(vTexCoord.y*size.y);

    vec4 color=vec4(1.0);

    if(y>value){
        int diff=y-int(value);
        color=mix(vec4(0.3,0.6,0.9,1.0),color,float(diff)/300.0);
    }

    FragColor=color;
}