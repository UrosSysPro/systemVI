#version 330 core

float box( in vec2 p, in vec2 b ){
    vec2 d = abs(p)-b;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}

out vec4 FragColor;

uniform sampler2D fontTexture;

struct Border{
    float radius,width;
    vec4 color;
};
in vec2 uv;
in struct{
    float x,y,width,height,rotation,blur;
    vec4 color,boundry;
    vec2 glyphUV;
    Border border;
}vertexOut;

void main(){
    float r=vertexOut.border.radius-vertexOut.blur;
    float blur=vertexOut.blur;
    vec2 b=vec2(vertexOut.width,vertexOut.height);
    vec2 p=vec2(vertexOut.x,vertexOut.y)*b;
    float border=box(p,b/2.0-r-blur)-r;
    b-=vertexOut.border.width*2.0;
    r-=vertexOut.border.width;
    float content=box(p,b/2.0-r-blur)-r;
    vec4 color=mix(vertexOut.color,vertexOut.border.color,smoothstep(0.0,blur,content));
    FragColor=vec4(color.rgb,(1.0-smoothstep(0.0,blur,border))*color.a)*texture(fontTexture,vertexOut.glyphUV);
}