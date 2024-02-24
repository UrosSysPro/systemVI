#version 330 core

in VERTEX_OUT{
    vec3 position;
    vec4 color;
    float borderRadius;
    float blur;
}vertexOut;

out vec4 FragColor;

float rect(vec2 p,vec2 b )
{
    vec2 d = abs(p)-b;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}

void main(){
    vec2 size=vec2(100.0);
    vec2 position=vertexOut.position.xy*size/2.0;
    float radius=40.0;
    float d=rect(position,size)+radius;

    vec4 color=vertexOut.color;
    if(d>0.0){
        FragColor=vec4(0.0);
    }else{
        FragColor=color;
    }
}