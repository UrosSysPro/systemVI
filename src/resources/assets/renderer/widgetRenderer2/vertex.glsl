#version 330 core

layout(location=0)in vec4 position;
layout(location=1)in vec4 rect;
layout(location=2)in float rectRotation;
layout(location=3)in vec4 color;
layout(location=4)in float borderRadius;
layout(location=5)in float borderWidth;
layout(location=6)in vec4 borderColor;
layout(location=7)in float blur;
layout(location=8)in vec4 glyph;
layout(location=9)in mat4 transform;
layout(location=13)in vec4 boundry;

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

struct Border{
    float radius,width;
    vec4 color;
};

out vec2 uv;

out struct{
    float x,y,width,height,rotation,blur;
    vec4 color,boundry;
    vec2 glyphUV;
    Border border;
}vertexOut;

void main(){
    vec4 worldPosition=translate(rect[0],rect[1])*rotate(rectRotation)*scale(rect[2],rect[3])*position;
    vec4 ndcPosition=projection*view*worldPosition;

    vertexOut.x=position.x;
    vertexOut.y=position.y;
    vertexOut.width=rect[2];
    vertexOut.height=rect[3];
    vertexOut.color=color;

    vertexOut.border.color=borderColor;
    vertexOut.border.width=borderWidth;
    vertexOut.border.radius=borderRadius;
    vertexOut.glyphUV=vec2(
        mix(glyph[0],glyph[2],position.x+0.5),
        mix(glyph[1],glyph[3],position.y+0.5)
    );
    vertexOut.boundry=boundry;
    vertexOut.blur=blur;

    uv=position.xy+0.5;

    gl_Position=ndcPosition;
}