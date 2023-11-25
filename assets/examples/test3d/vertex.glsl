#version 330 core

layout(location=0)in vec3 aPosition;
layout(location=1)in vec3 aNormal;
layout(location=2)in vec2 aUV;

out vec3 worldPosition;
out vec3 vNormal;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

void main(){
    mat3 normalRotation=mat3(model);
    normalRotation=transpose(inverse(normalRotation));

    vNormal=normalize(normalRotation*aNormal);
    vec4 tmp=model*vec4(aPosition,1.0);
    worldPosition=tmp.xyz;
    gl_Position=projection*view*model*vec4(aPosition,1.0);
}