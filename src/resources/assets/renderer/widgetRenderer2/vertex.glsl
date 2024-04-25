#version 330 core

layout(location=0)in vec4 position;
layout(location=1)in vec4 rect;
layout(location=2)in float rectRotation;

mat4 scale(float width,float height){
    return mat4(
    vec4(width,0.0,0.0,0.0),
    vec4(0.0,height,0.0,0.0),
    vec4(0.0,0.0,1.0,0.0),
    vec4(0.0,0.0,0.0,1.0)
    );
}

mat4 translate(float x,float y){
    return mat4(
    vec4(1.0,0.0,0.0,0.0),
    vec4(0.0,1.0,0.0,0.0),
    vec4(0.0,0.0,1.0,0.0),
    vec4(x,y,0.0,1.0)
    );
}

mat4 rotate(float angle){
    float c=cos(angle);
    float s=sin(angle);
    return mat4(
    vec4(  c, -s,0.0,0.0),
    vec4(  s,  c,0.0,0.0),
    vec4(0.0,0.0,1.0,0.0),
    vec4(0.0,0.0,0.0,1.0)
    );
}

uniform mat4 view;
uniform mat4 projection;

out struct{
    float x,y,width,height,rotation;

}vertexOut;

void main(){
    vec4 finalPosition=projection*view*translate(rect[0],rect[1])*rotate(rectRotation)*scale(rect[2],rect[3])*position;
    vertexOut.x=finalPosition.x;
    vertexOut.y=finalPosition.y;
    vertexOut.width=rect[2];
    vertexOut.height=rect[3];
    gl_Position=finalPosition;
}