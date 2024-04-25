#version 330 core

out vec4 FragColor;

struct Border{
    float radius,width;
    vec4 color;
};

in struct{
    float x,y,width,height,rotation,blur;
    vec4 color,glyph,boundry;
    Border border;
}vertexOut;

void main(){
//    FragColor=vec4(vertexOut.x,vertexOut.y,0.0,1.0);
    FragColor=vertexOut.border.color;
}