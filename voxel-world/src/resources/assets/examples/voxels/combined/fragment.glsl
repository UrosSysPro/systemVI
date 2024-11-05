#version 330

#define output(a) FragColor=a;return;

out vec4 FragColor;

in vec2 uv;

uniform float near;
uniform float far;

struct Camera {
    vec3 position;
    mat4 view, projection;
    float far,near;
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
    vec3 position,rotation;
    mat4 view,projection;
    float fov,aspect,near,far;
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

void blinPhong(vec3 lightDir, vec3 cameraDir, vec3 normal, Light light, out vec3 ambient, out vec3 diffuse, out vec3 specular) {
    //diffuse
    float diffuseF = max(0.0, dot(lightDir, normal));
    vec3 halfDir = normalize(lightDir + cameraDir);
    //specular
    float specularF = max(0.0, dot(halfDir, normal));
    specularF = pow(specularF, 32);
    //ambient
    float ambientF = 0.2;

    ambient=vec3(ambientF);
    diffuse=vec3(diffuseF);
    specular=vec3(specularF);
}

void main() {
    Light light = lightOf(vec4(1.0), vec3(0.0, 30.0, 0.0));
    vec2 screenSamplePoint = vec2(uv.x, 1.0 - uv.y);
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


//    vec4 projectionSpace=camera.projection*camera.view*vec4(position,1.0);
////    vec4 projectionSpace=camera.view*vec4(position,1.0);
//    float calculatedDepth=projectionSpace.z;
//    calculatedDepth/=projectionSpace.w;
////    calculatedDepth=(2.0 * camera.near) / (camera.far + camera.near - projectionSpace.z * (camera.far - camera.near));
//    output(vec4(calculatedDepth))

    //light i camera dir
    vec3 lightDir = normalize(light.position - position);
    vec3 cameraDir = normalize(camera.position - position);

    //reading value from shadow map
    vec4 shadowMapProjectionSpace=shadowMapInfo.projection*shadowMapInfo.view*vec4(position,1.0);
    shadowMapProjectionSpace.xyz/=vec3(shadowMapProjectionSpace.w);
    shadowMapProjectionSpace.xy*=vec2(0.5);
    shadowMapProjectionSpace.xy+=vec2(0.5);
//    output(vec4(shadowMapProjectionSpace))
    float shadowMapDepth=texture(shadowMap,shadowMapProjectionSpace.xy).x;
    float shadowNear=0.1,shadowFar=1000.0;
    shadowMapDepth=(2.0 * shadowNear) / (shadowFar + shadowNear - shadowMapDepth * (shadowFar - shadowNear));
    output(vec4(shadowMapDepth))
    //    float shadowMapProjectionDepth=0;
//    shadowMapProjectionDepth=(2.0 * shadowMapInfo.near) / (shadowMapInfo.far + shadowMapInfo.near - shadowMapProjectionSpace.z * (shadowMapInfo.far - shadowMapInfo.near));

    //shading
    vec3 ambient,diffuse,specular;
    blinPhong(lightDir,cameraDir,normal,light,ambient,diffuse,specular);
    vec3 color = (ambient + diffuse + specular) * occlusion * albedo.xyz;
    //vec3 color = (ambient + diffuse + specular) * occlusion * shadowMapProjectionSpace.xyz;
    //vec3 color = (ambient + diffuse + specular) * occlusion * vec3(shadowMapDepth);
    //vec3 color = (ambient + diffuse + specular) * occlusion * vec3(shadowMapProjectionDepth);

    if (depth > 0.99)color = skybox;

    FragColor = vec4(color, 1.0);
}