#version 330 core

in VERTEX_OUT{
    vec3 position;
    vec4 color;
    float borderRadius;
    float blur;
    vec2 size;
    float borderWidth;
    vec4 borderColor;
    vec4 clipRect;
}vertexOut;

out vec4 FragColor;

float rect(vec2 p,vec2 b )
{
    vec2 d = abs(p)-b;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}

void main(){
    vec2 size=vertexOut.size;
    vec2 position=vertexOut.position.xy*size;
    float radius=vertexOut.borderRadius;
    float blur=vertexOut.blur;
    float d=rect(position,size/2-radius-blur)-radius;
    vec4 color=vertexOut.color;
    float alpha=smoothstep(0.0,blur,d);

    FragColor=vec4(color.rgb,mix(color.a,0.0,alpha));
}