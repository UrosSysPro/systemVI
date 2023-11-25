#version 330 core

out vec4 pixel;

in vec3 worldPostion;
in vec3 vNormal;

uniform vec4 color;
uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform vec3 cameraPosition;

vec4 calculateLigting(vec3 normal,vec3 lightDirection,vec3 viewDirection){
    vec3 ambient=vec3(0.1,0.1,0.1);

    vec3 diffuse=vec3(max(dot(normal,lightDirection),0.0));

    vec3 reflectedLightDir=reflect(-lightDirection,normal);
    vec3 specular=vec3(max(pow(dot(viewDirection,reflectedLightDir),128.0),0.0));

    return vec4(ambient+diffuse+specular,1.0);
}

void main(){
    //dot(v1,v2);  x1*x2+y1*y2+z1*z2   |v1|*|v2|*cos(teta)
    //cross(v1,v2);
    //v1*v2;  vec3(x1*x2,y1*y2,z1*z2);
    vec3 lightDirection=normalize(lightPosition-worldPostion);
    vec3 viewDirection=normalize(cameraPosition-worldPostion);

    pixel=calculateLigting(vNormal,lightDirection,viewDirection)*color;
}