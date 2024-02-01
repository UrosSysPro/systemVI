#version 330 core

in vec2 vTexCoord;

uniform sampler2D t0;
uniform sampler2D normalBuffer;
uniform sampler2D positionBuffer;
uniform sampler2D depthBuffer;
uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;

uniform vec3 cameraPosition;
uniform vec3 lightPosition;

vec4 phongLighting(vec2 uv,vec3 normal,vec3 lightDirection,vec3 viewDirection){
    vec3 ambient=vec3(0.3,0.3,0.3);

    vec3 diffuse=vec3(max(dot(normal,lightDirection),0.0));

    vec3 reflectedLightDir=reflect(-lightDirection,normal);
    vec3 specular=vec3(max(pow(dot(viewDirection,reflectedLightDir),128.0),0.0))*texture(specularTexture,uv).rgb;

    return vec4(ambient+diffuse+specular,1.0);
}

out vec4 FragColor;

void main(){
    vec2 uv=texture(t0,vTexCoord).xy;
    vec3 normal=normalize(texture(normalBuffer,vTexCoord).xyz*2.0-1.0);

    vec3 position=texture(positionBuffer,vTexCoord).xyz;

    vec3 lightDirection=normalize(lightPosition-position);
    vec3 viewDirection=normalize(cameraPosition-position);

    float z=texture(depthBuffer,vTexCoord).r;
    float near=0.1,far=1000.0;
    float depth= (2.0 * near) / (far + near - z * (far - near));

    vec4 fogColor=vec4(0.3,0.6,0.9,1.0);

    vec4 lighting=phongLighting(uv,normal,lightDirection,viewDirection);

    vec4 diffuse=texture(diffuseTexture,uv);

    FragColor=mix(diffuse*lighting,fogColor,depth);
}