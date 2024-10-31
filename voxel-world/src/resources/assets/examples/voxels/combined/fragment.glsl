#version 330

out vec4 FragColor;

in vec2 uv;

struct Camera{
    vec3 position;
};
struct Light{
    vec4 color;
    vec3 position;
};
Light lightOf(vec4 color,vec3 position){
    Light light;
    light.color=color;
    light.position=position;
    return light;
}

uniform sampler2D positionBuffer;
uniform sampler2D normalBuffer;
uniform sampler2D tangentBuffer;
uniform sampler2D uvBuffer;
uniform sampler2D depthBuffer;
uniform sampler2D occlusionBuffer;
uniform sampler2D diffuseMap;
uniform sampler2D normalMap;
uniform Camera camera;

void main(){
    Light light=lightOf(vec4(1.0),vec3(0.0,30.0,0.0));
    vec2 samplepoint=vec2(uv.x,1.0-uv.y);
    vec3 normal=texture(normalBuffer,samplepoint).xyz;
    vec3 position=texture(positionBuffer,samplepoint).xyz;
    samplepoint=texture(uvBuffer,samplepoint).xy;
    //albedo
    vec4 albedo=texture(diffuseMap,samplepoint);
    //diffuse
    vec3 lightDir=normalize(light.position-position);
    float diffuse=max(0.0,dot(lightDir,normal));
    //specular
    vec3 cameraDir=normalize(camera.position-position);
    vec3 halfDir=normalize(lightDir+cameraDir);
    float specular=max(0.0,dot(halfDir,normal));
    specular=pow(specular,32);
    //ambient
    float ambient=0.2;

    vec4 color=(ambient+(diffuse+specular)*light.color)*albedo;

    FragColor=color;
}