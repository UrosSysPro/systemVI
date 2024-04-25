#version 330 core

out vec4 FragColor;


in struct{
    float x,y,width,height,rotation;
}vertexOut;

void main(){
    FragColor=vec4(vertexOut.x/vertexOut.width,vertexOut.y/vertexOut.height,0.0,1.0);
}