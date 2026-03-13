#version 330

in vec3 worldPosition;
in vec3 vNormal;

out vec4 FragColor;

uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 ambientColor;
uniform vec3 diffuseColor;
uniform vec3 specularColor;
uniform float shininess;
uniform vec3 cameraPosition;

void main(){
    vec3 normal = normalize(vNormal);
    vec3 lightDir = normalize(lightPosition - worldPosition);
    vec3 viewDir = normalize(cameraPosition - worldPosition);
    vec3 halfDir = normalize(lightDir + viewDir);

    // ambient
    vec3 ambient = ambientColor;

    // diffuse
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = diff * diffuseColor * lightColor;

    // specular (Blinn-Phong)
    float spec = pow(max(dot(normal, halfDir), 0.0), shininess);
    vec3 specular = spec * specularColor * lightColor;

//    vec3 result = ambient + diffuse + specular;
    vec3 result = ambient + diffuse;
    FragColor = vec4(result, 1.0);
}