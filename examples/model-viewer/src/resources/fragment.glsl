#version 330 core

out vec4 FragColor;

uniform sampler2D colormap;
uniform vec3 cameraPosition;

in struct VERTEX_OUT{
    vec3 position;
    vec3 normal;
    vec2 uv;
} vertexOut;

struct Light{
    vec3 color;
    vec3 position;
    vec3 attenuation;
};

Light lightOf(vec3 color, vec3 position, vec3 attenuation){
    Light l;
    l.color=color;
    l.position=position;
    l.attenuation=attenuation;
    return l;
}


void main(){

    Light light=lightOf(vec3(1.0),vec3(2.0,2.0,2.0),vec3(0,0,1));
    vec3 lightDir=normalize(light.position-vertexOut.position);

    vec3 normal=normalize(vertexOut.normal);
    vec2 uv=vertexOut.uv;
    vec3 albedo=texture(colormap,uv).xyz;
    float diffuse=max(dot(lightDir,normal),0);

    vec3 cameraDir=normalize(cameraPosition-vertexOut.position);
    vec3 average=normalize((cameraDir+lightDir)/2);

    float specular=max(0.0,dot(average,normal));
    specular=pow(specular,128);

    float ambient=0.2;

    vec3 color=vec3(diffuse+specular+ambient)*light.color*albedo;

    FragColor=vec4(color,1.0);

//    FragColor=vec4(vertexOut.normal,1.0);
}