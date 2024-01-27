#version 330 core

out vec4 FragColor;
in vec2 vTexCoord;

vec2 multiply(vec2 a,vec2 b){
    vec2 result=vec2(
        a.x*b.x-a.y*b.y,
        a.x*b.y+a.y*b.x
    );
    return result;
}

int mandelbrot(vec2 c){
    vec2 z=vec2(0.0);
    int n=0;
    for(int i=0;i<50;i+=1){
        z=multiply(z,z)+c;
        n+=1;
        if(length(z)>2.0)break;
    }
    return n;
}

uniform vec2 position;
uniform float zoom;
uniform float aspect;

void main(){
    vec2 pos=vTexCoord;
    pos-=0.5;
    pos*=zoom;
    pos.x*=aspect;
    pos+=position;

    int value=mandelbrot(pos);
    vec4 color=vec4(float(value)/50.0);
    FragColor=color;
}