#version 330

out vec4 FragColor;
uniform sampler2D textureBuff;
uniform vec2 size;
in vec2 uv;

mat4 rotateX(float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return mat4(
    vec4(1.0, 0.0, 0.0, 0.0),
    vec4(0.0, c, -s, 0.0),
    vec4(0.0, s, c, 0.0),
    vec4(0.0, 0.0, 0.0, 1.0)
    );
}
mat4 rotateY(float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return mat4(
    vec4(c, 0.0, -s, 0.0),
    vec4(0.0, 1.0, 0.0, 0.0),
    vec4(s, 0.0, c, 0.0),
    vec4(0.0, 0.0, 0.0, 1.0)
    );
}
mat4 rotateZ(float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return mat4(
    vec4(c, -s, 0.0, 0.0),
    vec4(s, c, 0.0, 0.0),
    vec4(0.0, 0.0, 1.0, 0.0),
    vec4(0.0, 0.0, 0.0, 1.0)
    );
}

vec3 reinhardToneMapping(vec3 color) {
    return color / (1.0 + color);
}

vec3 uncharted2ToneMapping(vec3 color) {
    float A = 0.15;
    float B = 0.50;
    float C = 0.10;
    float D = 0.20;
    float E = 0.02;
    float F = 0.30;

    color = ((color * (A * color + C * B) + D * E) / (color * (A * color + B) + D * F)) - E / F;
    return color;
}

vec3 acesFilmToneMapping(vec3 color) {
    float a = 2.51;
    float b = 0.03;
    float c = 2.43;
    float d = 0.59;
    float e = 0.14;

    color = (color * (a * color + b)) / (color * (c * color + d) + e);
    return color;
}
// Exponential tone mapping shader code
vec3 exponentialToneMapping(vec3 color, float exposure) {
    return vec3(1.0) - exp(-color * exposure);
}

vec3 whiteBalance(vec3 color, float temperature) {
    // Adjust colors based on temperature value
    float temp = clamp(temperature, 0.0, 1.0);
    color.r = mix(color.r, color.r * (1.0 + temp), temp);
    color.b = mix(color.b, color.b * (1.0 - temp), temp);
    return color;
}

vec3 adjustSaturation(vec3 color, float saturation) {
    float gray = dot(color, vec3(0.3, 0.59, 0.11));// luminance (approximation)
    return mix(vec3(gray), color, saturation);
}

vec3 adjustBrightnessContrast(vec3 color, float brightness, float contrast) {
    return (color - 0.5) * contrast + 0.5 + brightness;
}

vec3 adjustHue(vec3 color, float hue) {
    float angle = radians(hue);
    float s = sin(angle);
    float c = cos(angle);
    mat3 hueRotation = mat3(
    vec3(0.213 + c * 0.787 - s * 0.213, 0.213 - c * 0.213 + s * 0.143, 0.213 - c * 0.213 - s * 0.787),
    vec3(0.715 - c * 0.715 - s * 0.715, 0.715 + c * 0.285 + s * 0.140, 0.715 - c * 0.715 + s * 0.715),
    vec3(0.072 - c * 0.072 + s * 0.928, 0.072 - c * 0.072 - s * 0.283, 0.072 + c * 0.928 + s * 0.072)
    );
    return color * hueRotation;
}

vec3 colorRotation(vec3 color, vec3 rotation) {
    mat4 mat = rotateX(rotation.x) * rotateY(rotation.y) * rotateZ(rotation.z);
    vec4 result = mat * vec4(color - vec3(0.5), 1.0);
    return result.xyz + 0.5;
}

//gamma correction
vec3 gammaCorrection(vec3 color, float gamma) {
    return pow(color, vec3(1.0 / gamma));
}


void main() {
    //read from buffer
    vec3 hdrColor = texture(textureBuff, uv).rgb;

    //tone mapping
//    vec3 mapped = hdrColor;
        vec3 mapped = acesFilmToneMapping(hdrColor);
    //    vec3 mapped = exponentialToneMapping(hdrColor);
    //    vec3 mapped = uncharted2ToneMapping(hdrColor);
    //    vec3 mapped = reinhardToneMapping(hdrColor);

    //color correction
//    vec3 corrected = mapped;
    //    vec3 corrected = colorRotation(mapped,vec3(0.0,0.0,1.0));
        vec3 corrected = whiteBalance(mapped, 0.2);
    //    vec3 corrected=adjustSaturation(mapped,2);
    //    vec3 corrected=adjustBrightnessContrast(mapped,0.3,2);
    //    vec3 corrected=adjustHue(mapped,100);

    //gamma correction
//    vec3 gammaCorrected=corrected;
        vec3 gammaCorrected = gammaCorrection(corrected, 2.2);
    //    vec3 gammaCorrected=gammaCorrection(corrected,0.5);
    //    vec3 gammaCorrected=gammaCorrection(corrected,3.5);

    FragColor = vec4(gammaCorrected, 1.0);
}