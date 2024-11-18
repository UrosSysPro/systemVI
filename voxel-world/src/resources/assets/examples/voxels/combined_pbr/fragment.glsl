#version 330

out vec4 FragColor;

in vec2 uv;

uniform float near;
uniform float far;

struct Camera {
    vec3 position;
    mat4 view, projection;
    float far, near;
};

struct Light {
    vec4 color;
    vec3 position;
};

Light lightOf(vec4 color, vec3 position) {
    Light light;
    light.color = color;
    light.position = position;
    return light;
}

struct ShadowMapInfo {
    vec3 position, rotation, attenuation, color;
    mat4 view, projection;
    float fov, aspect, near, far,bias;
};

uniform sampler2D positionBuffer;
uniform sampler2D normalBuffer;
uniform sampler2D tangentBuffer;
uniform sampler2D uvBuffer;
uniform sampler2D depthBuffer;
uniform sampler2D occlusionBuffer;
uniform sampler2D diffuseMap;
uniform sampler2D normalMap;
uniform sampler2D skybox;
uniform sampler2D shadowMap;
uniform Camera camera;
uniform ShadowMapInfo shadowMapInfo;

float metalic=0.5;
float roughness=0.5;


void blinPhong(vec3 lightDir, vec3 cameraDir, vec3 normal, Light light, out vec3 ambient, out vec3 diffuse, out vec3 specular) {
    //diffuse
    float diffuseF = max(0.0, dot(lightDir, normal));
    vec3 halfDir = normalize(lightDir + cameraDir);
    //specular
    float specularF = max(0.0, dot(halfDir, normal));
    specularF = pow(specularF, 32);
    //ambient
    float ambientF = 0.2;

    ambient = vec3(ambientF);
    diffuse = vec3(diffuseF);
    specular = vec3(specularF);
}

float calculateAttenuation(vec3 attenuation,vec3 position, vec3 lightPosition){
    float x=distance(position,lightPosition);
    return 1.0/(x*x*attenuation.x+x*attenuation.y+attenuation.z);
}




void main() {
    Light light = lightOf(vec4(1.0), shadowMapInfo.position);
    vec2 screenSamplePoint = vec2(uv.x, uv.y);
    vec2 uvSamplePoint = texture(uvBuffer, screenSamplePoint).xy;

    //normal tangent bitangent
    vec3 normal = texture(normalBuffer, screenSamplePoint).xyz;
    vec3 tangent = texture(tangentBuffer, screenSamplePoint).xyz;
    vec3 bitangent = cross(normal, tangent);
    mat3 tbn = mat3(tangent, bitangent, normal);
    normal = tbn * (texture(normalMap, uvSamplePoint).xyz * vec3(2.0) - vec3(1.0));

    //depth from depth buffer
    float depth = texture(depthBuffer, screenSamplePoint).r;
    depth = (2.0 * near) / (far + near - depth * (far - near));

    //value from skybox
    vec3 skybox = texture(skybox, screenSamplePoint).xyz;

    //position occlusion albedo
    vec3 position = texture(positionBuffer, screenSamplePoint).xyz;
    float occlusion = texture(occlusionBuffer, screenSamplePoint).x;
    vec4 albedo = texture(diffuseMap, uvSamplePoint);

    //light i camera dir
    vec3 lightDir = normalize(light.position - position);
    vec3 cameraDir = normalize(camera.position - position);

    //reading value from shadow map
    vec4 shadowMapProjectionSpace = shadowMapInfo.projection * shadowMapInfo.view * vec4(position, 1.0);
    shadowMapProjectionSpace.xyz /= vec3(shadowMapProjectionSpace.w);
    shadowMapProjectionSpace.xyz *= vec3(0.5);
    shadowMapProjectionSpace.xyz += vec3(0.5);

    float shadowNear = shadowMapInfo.near, shadowFar = shadowMapInfo.far;
    float screenDepth = shadowMapProjectionSpace.z;

    float mapDepth = texture(shadowMap, shadowMapProjectionSpace.xy).r;
    float inShadow=float(screenDepth-shadowMapInfo.bias<mapDepth);

    //blin-phong shading
    vec3 ambient, diffuse, specular;
    blinPhong(lightDir, cameraDir, normal, light, ambient, diffuse, specular);
    float attenuation=calculateAttenuation(shadowMapInfo.attenuation,position,shadowMapInfo.position);
    vec3 color = (ambient + (diffuse + specular)*vec3(inShadow)*vec3(attenuation)*shadowMapInfo.color) * vec3(occlusion) * albedo.xyz;

    //pbr shading


    if (depth > 0.99)color = skybox;

    FragColor = vec4(color, 1.0);
}