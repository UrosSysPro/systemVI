#version 330

out vec4 FragColor;

in vec2 uv;

struct Camera {
    vec3 position;
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

uniform sampler2D positionBuffer;
uniform sampler2D normalBuffer;
uniform sampler2D tangentBuffer;
uniform sampler2D uvBuffer;
uniform sampler2D depthBuffer;
uniform sampler2D occlusionBuffer;
uniform sampler2D diffuseMap;
uniform sampler2D normalMap;
uniform sampler2D skybox;
uniform Camera camera;

vec3 blinPhong(vec3 lightDir, vec3 cameraDir, vec3 normal, Light light) {
    float diffuse = max(0.0, dot(lightDir, normal));
    vec3 halfDir = normalize(lightDir + cameraDir);
    float specular = max(0.0, dot(halfDir, normal));
    specular = pow(specular, 32);
    //ambient
    float ambient = 0.2;
    return vec3(ambient) + vec3(diffuse + specular) * light.color.rgb;
}

void main() {
    Light light = lightOf(vec4(1.0), vec3(0.0, 30.0, 0.0));
    vec2 screenSamplePoint = vec2(uv.x, 1.0 - uv.y);
    vec2 uvSamplePoint = texture(uvBuffer, screenSamplePoint).xy;

    vec3 normal = texture(normalBuffer, screenSamplePoint).xyz;
    vec3 tangent = texture(tangentBuffer, screenSamplePoint).xyz;
    vec3 bitangent = cross(normal, tangent);
    mat3 tbn = mat3(tangent, bitangent, normal);

    float near = 0.1, far = 100.0;
    float depth = texture(depthBuffer, screenSamplePoint).r;
    depth = (2.0 * near) / (far + near - depth * (far - near));

    vec3 skybox = texture(skybox, screenSamplePoint).xyz;

    vec3 position = texture(positionBuffer, screenSamplePoint).xyz;
    float occlusion = texture(occlusionBuffer, screenSamplePoint).x;
    vec4 albedo = texture(diffuseMap, uvSamplePoint);
    normal = tbn * (texture(normalMap, uvSamplePoint).xyz * vec3(2.0) - vec3(1.0));

    vec3 lightDir = normalize(light.position - position);
    vec3 cameraDir = normalize(camera.position - position);

    vec3 color = blinPhong(lightDir, cameraDir, normal, light) * occlusion * albedo.xyz;
//    vec3 color = blinPhong(lightDir, cameraDir, normal, light)*albedo.xyz;

    if (depth > 0.99)color = skybox;

    FragColor = vec4(color, 1.0);
}