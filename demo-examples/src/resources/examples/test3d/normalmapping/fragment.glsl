#version 330 core

out vec4 pixel;

in vec3 worldPosition;
in vec3 vNormal;
in vec3 vTangent;
in vec3 vBiTangent;
in vec2 vUV;

uniform vec4 color;
uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform vec3 cameraPosition;

uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D ambientTexture;
uniform sampler2D normalTexture;

vec4 calculateLigting(vec3 normal,vec3 lightDirection,vec3 viewDirection){
    vec3 ambient=vec3(0.1,0.1,0.1);

    vec3 diffuse=vec3(max(dot(normal,lightDirection),0.0));

    vec3 reflectedLightDir=reflect(-lightDirection,normal);
    vec3 specular=vec3(max(pow(dot(viewDirection,reflectedLightDir),128.0),0.0))*texture(specularTexture,vUV).rgb;

    float ambientOclusion=texture(ambientTexture,vUV).r;
    return vec4(ambient+(diffuse+specular)*ambientOclusion,1.0);
}

void main(){
    //dot(v1,v2);  x1*x2+y1*y2+z1*z2   |v1|*|v2|*cos(teta)
    //cross(v1,v2);
    //v1*v2;  vec3(x1*x2,y1*y2,z1*z2);
    vec3 lightDirection=normalize(lightPosition-worldPosition);
    vec3 viewDirection=normalize(cameraPosition-worldPosition);

    mat3 TBN=mat3(vTangent,vBiTangent,vNormal);

    vec3 mappedNormal=normalize(TBN*(texture(normalTexture,vUV).xyz*2.0-1.0));

    pixel=calculateLigting(mappedNormal,lightDirection,viewDirection)*texture(diffuseTexture,vUV);
//    pixel=vec4(mappedNormal,1.0);
//    pixel=vec4(vNormal,1.0);
//    pixel=vec4(worldPosition,1.0);
//    pixel=vec4(abs(vNormal),1.0);
}